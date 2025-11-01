package gnu.project.backend.cart.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.cart.dto.request.CartAddRequest;
import gnu.project.backend.cart.dto.request.CartBulkDeleteRequest;
import gnu.project.backend.cart.dto.request.CartItemUpdateRequest;
import gnu.project.backend.cart.dto.response.CartItemResponse;
import gnu.project.backend.cart.dto.response.CartSummaryResponse;
import gnu.project.backend.cart.entity.Cart;
import gnu.project.backend.cart.entity.CartItem;
import gnu.project.backend.cart.repository.CartItemRepository;
import gnu.project.backend.cart.repository.CartRepository;
import gnu.project.backend.common.exception.AuthException;
import gnu.project.backend.common.exception.BusinessException;
import gnu.project.backend.customer.entity.Customer;
import gnu.project.backend.customer.repository.CustomerRepository;
import gnu.project.backend.product.entity.Option;
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.OptionRepository;
import gnu.project.backend.product.repository.ProductRepository;
import gnu.project.backend.reservation.entity.Reservation;
import gnu.project.backend.reservation.enumerated.Status;
import gnu.project.backend.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static gnu.project.backend.common.error.ErrorCode.*;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OptionRepository optionRepository;
    private final ReservationRepository reservationRepository;

    private Customer getCurrentCustomer(Accessor accessor) {
        return customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));
    }

    private Cart getOrCreateCart(Customer customer) {
        return cartRepository.findByCustomer_Id(customer.getId())
                .orElseGet(() -> cartRepository.save(Cart.create(customer)));
    }

    @Transactional
    public void addItem(Accessor accessor, CartAddRequest request) {
        Customer customer = getCurrentCustomer(accessor);
        Cart cart = getOrCreateCart(customer);

        Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException(PRODUCT_NOT_FOUND_EXCEPTION));

        Option option = null;
        if (request.optionId() != null && request.optionId() != 0) {   // 프론트가 0 보내는 케이스 방지
            option = optionRepository.findById(request.optionId())
                    .orElseThrow(() -> new BusinessException(OPTION_NOT_FOUND_EXCEPTION));
        }

        int qty = (request.quantity() != null && request.quantity() > 0)
                ? request.quantity()
                : 1;

        CartItem sameItem = cartItemRepository.findSameItem(
                cart.getId(),
                request.productId(),
                request.optionId(),
                request.desireDate()
        );
        if (sameItem != null) {
            sameItem.updateQuantity(sameItem.getQuantity() + qty);
            return;
        }

        CartItem cartItem = CartItem.create(
                cart,
                product,
                option,
                qty,
                request.desireDate(),
                request.memo()
        );

        cartItemRepository.save(cartItem);
    }

    @Transactional(readOnly = true)
    public CartSummaryResponse readMyCart(Accessor accessor) {
        String socialId = accessor.getSocialId();
        List<CartItem> items = cartItemRepository.findAllByCustomerSocialId(socialId);

        List<CartItemResponse> responses = items.stream()
                .map(CartItemResponse::from)
                .toList();

        int totalProductAmount = items.stream()
                .filter(CartItem::isSelected)
                .mapToInt(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();

        int totalDiscountAmount = 0;
        int paymentAmount = totalProductAmount - totalDiscountAmount;

        return new CartSummaryResponse(
                responses,
                totalProductAmount,
                totalDiscountAmount,
                paymentAmount
        );
    }

    @Transactional
    public void updateCartItem(
            Long cartItemId,
            Accessor accessor,
            CartItemUpdateRequest request
    ) {
        Customer current = getCurrentCustomer(accessor);

        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(CART_ITEM_NOT_FOUND));

        if (!item.getCart().getCustomer().getId().equals(current.getId())) {
            throw new AuthException(AUTH_FORBIDDEN);
        }

        if (request.quantity() != null) {
            item.updateQuantity(request.quantity());
        }
        if (request.selected() != null) {
            item.updateSelected(request.selected());
        }
    }

    @Transactional
    public void deleteCartItem(Long cartItemId, Accessor accessor) {
        Customer current = getCurrentCustomer(accessor);
        CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(CART_ITEM_NOT_FOUND));
        if (!item.getCart().getCustomer().getId().equals(current.getId())) {
            throw new AuthException(AUTH_FORBIDDEN);
        }
        cartItemRepository.delete(item);
    }

    @Transactional
    public void bulkDelete(CartBulkDeleteRequest request, Accessor accessor) {
        Customer current = getCurrentCustomer(accessor);
        List<CartItem> items = cartItemRepository.findAllById(request.cartItemIds());
        for (CartItem item : items) {
            if (item.getCart().getCustomer().getId().equals(current.getId())) {
                cartItemRepository.delete(item);
            }
        }
    }

    @Transactional
    public void toggleSelectAll(Accessor accessor, boolean selected) {
        String socialId = accessor.getSocialId();
        List<CartItem> items = cartItemRepository.findAllByCustomerSocialId(socialId);
        items.forEach(i -> i.updateSelected(selected));
    }

    @Transactional
    public void createReservationsFromSelected(Accessor accessor) {
        Customer customer = getCurrentCustomer(accessor);

        List<CartItem> selectedItems =
                cartItemRepository.findSelectedByCustomerSocialId(accessor.getSocialId());

        for (CartItem item : selectedItems) {
            var product = item.getProduct();

            StringBuilder contentBuilder = new StringBuilder();

            if (item.getOption() != null) {
                contentBuilder.append("[옵션] ").append(item.getOption().getName());
            }
            if (item.getMemo() != null && !item.getMemo().isEmpty()) {
                if (contentBuilder.length() > 0) {
                    contentBuilder.append(" / ");
                }
                contentBuilder.append(item.getMemo());
            }

            Reservation reservation = Reservation.ofCreate(
                    product.getOwner(),
                    customer,
                    product,
                    Status.PENDING,
                    item.getDesireDate() != null ? item.getDesireDate().toLocalDate() : null,
                    product.getName(),
                    contentBuilder.length() > 0 ? contentBuilder.toString() : null
            );

            reservationRepository.save(reservation);

            // 예약으로 보냈으니 장바구니에서는 제거
            cartItemRepository.delete(item);
        }
    }
}
