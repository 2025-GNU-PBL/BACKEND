package gnu.project.backend.cart.service;

import static gnu.project.backend.common.error.ErrorCode.*;

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
import gnu.project.backend.product.entity.Product;
import gnu.project.backend.product.repository.ProductRepository;
import gnu.project.backend.reservation.prefill.dto.response.CreateDraftsResponse;
import gnu.project.backend.reservation.prefill.service.ReservationPrefillService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final ReservationPrefillService prefillService;

    private Customer getCurrentCustomer(Accessor accessor) {
        return customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));
    }

    private Cart getOrCreateCart(Customer customer) {
        return cartRepository.findByCustomer_Id(customer.getId())
                .orElseGet(() -> cartRepository.save(Cart.create(customer)));
    }

    public void addItem(Accessor accessor, CartAddRequest request) {
        final Customer customer = getCurrentCustomer(accessor);
        final Cart cart = getOrCreateCart(customer);

        final Product product = productRepository.findById(request.productId())
                .orElseThrow(() -> new BusinessException(PRODUCT_NOT_FOUND_EXCEPTION));

        final int qty = (request.quantity() != null && request.quantity() > 0) ? request.quantity() : 1;

        final CartItem existing = cartItemRepository.findSameItem(
                cart.getId(),
                request.productId(),
                request.desireDate()
        );

        if (existing != null) {
            existing.updateQuantity(existing.getQuantity() + qty);
            return;
        }

        final CartItem cartItem = CartItem.create(
                cart,
                product,
                qty,
                request.desireDate()
        );
        cartItemRepository.save(cartItem);
    }

    @Transactional(readOnly = true)
    public CartSummaryResponse readMyCart(Accessor accessor) {
        final String socialId = accessor.getSocialId();
        final List<CartItem> items = cartItemRepository.findAllByCustomerSocialId(socialId);

        final List<CartItemResponse> responses = items.stream()
                .map(CartItemResponse::from)
                .toList();

        final int totalProductAmount = calcSelectedTotalAmount(items);
        final int totalDiscountAmount = 0;
        final int paymentAmount = totalProductAmount - totalDiscountAmount;

        return new CartSummaryResponse(responses, totalProductAmount, totalDiscountAmount, paymentAmount);
    }

    public void updateCartItem(Long cartItemId, Accessor accessor, CartItemUpdateRequest request) {
        final Customer current = getCurrentCustomer(accessor);
        final CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(CART_ITEM_NOT_FOUND));

        assertOwnedByCustomer(item, current.getId());

        if (request.quantity() != null) item.updateQuantity(request.quantity());
        if (request.selected() != null) item.updateSelected(request.selected());
    }

    public void deleteCartItem(Long cartItemId, Accessor accessor) {
        final Customer current = getCurrentCustomer(accessor);
        final CartItem item = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new BusinessException(CART_ITEM_NOT_FOUND));
        assertOwnedByCustomer(item, current.getId());
        cartItemRepository.delete(item);
    }

    public void bulkDelete(CartBulkDeleteRequest request, Accessor accessor) {
        final Customer current = getCurrentCustomer(accessor);
        final List<CartItem> items = cartItemRepository.findAllById(request.cartItemIds());
        for (CartItem item : items) {
            if (item.getCart().getCustomer().getId().equals(current.getId())) {
                cartItemRepository.delete(item);
            }
        }
    }

    public void toggleSelectAll(Accessor accessor, boolean selected) {
        final String socialId = accessor.getSocialId();
        final List<CartItem> items = cartItemRepository.findAllByCustomerSocialId(socialId);
        items.forEach(i -> i.updateSelected(selected));
    }

    public CreateDraftsResponse createInquiryDraftsFromSelected(final Accessor accessor) {
        final Customer customer = getCurrentCustomer(accessor);
        final List<CartItem> selectedItems =
                cartItemRepository.findSelectedByCustomerSocialId(accessor.getSocialId());

        if (selectedItems.isEmpty()) {
            // 전용 에러코드가 있으면 바꿔도 됨
            throw new BusinessException(CART_ITEM_NOT_FOUND);
        }

        final List<Product> products = new ArrayList<>(selectedItems.size());
        final List<Integer> quantities = new ArrayList<>(selectedItems.size());
        final List<java.time.LocalDate> desiredDates = new ArrayList<>(selectedItems.size());

        for (CartItem item : selectedItems) {
            products.add(item.getProduct());
            quantities.add(item.getQuantity());
            java.time.LocalDate date = (item.getDesireDate() != null)
                    ? item.getDesireDate().toLocalDate()
                    : null;
            desiredDates.add(date);
        }

        // expiresAt 등 메타를 포함한 DTO로 반환 (프런트에서 만료 타이머/안내 처리 용이)
        return prefillService.createFromCartItems(
                customer, products, quantities, desiredDates
        );
    }

    private int calcSelectedTotalAmount(List<CartItem> items) {
        return items.stream()
                .filter(CartItem::isSelected)
                .mapToInt(i -> i.getProduct().getPrice() * i.getQuantity())
                .sum();
    }

    private void assertOwnedByCustomer(CartItem item, Long customerId) {
        if (!item.getCart().getCustomer().getId().equals(customerId)) {
            throw new AuthException(AUTH_FORBIDDEN);
        }
    }
}
