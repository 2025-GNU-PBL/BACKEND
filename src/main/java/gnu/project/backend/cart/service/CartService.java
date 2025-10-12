package gnu.project.backend.cart.service;


import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.cart.dto.request.AddCartItemRequest;
import gnu.project.backend.cart.dto.response.CartItemResponse;
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
import gnu.project.backend.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static gnu.project.backend.common.error.ErrorCode.AUTH_FORBIDDEN;
import static gnu.project.backend.common.error.ErrorCode.CUSTOMER_NOT_FOUND_EXCEPTION;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    //private final ProductRepository productRepository;
    //private final OptionRepository optionRepository;

    public void addCart(Accessor accessor, AddCartItemRequest request) {
        //1. 사용자 조회
        Customer customer = customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));

        //2. 상품 조회
//        Product product = productRepository.findById(request.getProductId())
//                .orElseThrow(() -> new IllegalArgumentException("상품을 찾을 수 없습니다."));

        //3. 옵션 조회
//       Option option = (request.getOptionId() != null)
//                ? optionRepository.findById(request.getOptionId()).orElse(null)
//                : null;

        //4. 장바구니 아이템 생성 저장

        // 5. 장바구니에 상품 항목(CartItem)을 생성하여 저장
        Cart cart = cartRepository.findByCustomer_Id(customer.getId())
                .orElseGet(() -> cartRepository.save(Cart.create(customer)));



        CartItem cartItem = CartItem.create(
                cart,
                null,
                null,
                request.getQuantity(),
                request.getDesireDate(),
                request.getMemo());

        cartItemRepository.save(cartItem);
    }

    //장바구니 조회
    @Transactional(readOnly = true)
    public List<CartItemResponse> getMyCart(Accessor accessor) {

        return customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .map(customer ->
                        cartRepository.findByCustomer_Id(customer.getId())
                                .map(cart ->
                                        cartItemRepository.findByCartId(cart.getId()).stream()
                                                .map(CartItemResponse::new)
                                                .toList()
                                )
                                .orElse(Collections.emptyList())
                )
                .orElse(Collections.emptyList());
    }

    public void deleteCartItem(Long cartItemId, Accessor accessor) throws Throwable {
        // 1. 삭제할 장바구니 항목 조회
        CartItem cartItem = (CartItem) cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new IllegalArgumentException("장바구니에 해당 상품이 없습니다."));

        // 2. 현재 로그인한 사용자를 조회
        Customer currentCustomer = customerRepository.findByOauthInfo_SocialId(accessor.getSocialId())
                .orElseThrow(() -> new BusinessException(CUSTOMER_NOT_FOUND_EXCEPTION));

        if (!cartItem.getCart().getCustomer().getId().equals(currentCustomer.getId())) {
            throw new AuthException(AUTH_FORBIDDEN);
        }

        // 4. 본인 확인 후 항목 삭제
        cartItemRepository.delete(cartItem);
    }


}
