package gnu.project.backend.product.service;

import gnu.project.backend.auth.entity.Accessor;
import gnu.project.backend.product.dto.response.ProductPageResponse;
import gnu.project.backend.product.dto.response.RecentSearchResponse;
import gnu.project.backend.product.enumerated.SortType;
import gnu.project.backend.product.repository.ProductSearchRepository;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductSearchService {

    private static final String SEARCH_KEY_PREFIX = "recent_search:";
    private static final int MAX_SEARCH_HISTORY = 10;
    private final ProductSearchRepository productSearchRepository;
    private final RedisTemplate<String, String> redisTemplate;

    @Transactional(readOnly = true)
    public Page<ProductPageResponse> search(
        final String keyword,
        final SortType sortType,
        final Integer pageNumber,
        final Integer pageSize
    ) {
        return productSearchRepository.searchAll(
            keyword,
            sortType,
            pageNumber,
            pageSize
        );
    }

    public RecentSearchResponse saveSearchKeyword(final Accessor accessor, final String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return getRecentSearches(accessor);
        }

        String key = generateKey(accessor.getSocialId(), accessor.getUserRole().name());
        double score = Instant.now().toEpochMilli();

        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        zSetOps.remove(key, keyword);
        zSetOps.add(key, keyword, score);
        
        long size = Optional.of(zSetOps.size(key)).orElse(0L);
        if (size > MAX_SEARCH_HISTORY) {
            zSetOps.removeRange(key, 0, size - MAX_SEARCH_HISTORY - 1);
        }

        return getRecentSearches(accessor);
    }

    public RecentSearchResponse getRecentSearches(final Accessor accessor) {
        final String key = generateKey(accessor.getSocialId(), accessor.getUserRole().name());
        ZSetOperations<String, String> zSetOps = redisTemplate.opsForZSet();

        Set<String> searches = zSetOps.reverseRange(key, 0, MAX_SEARCH_HISTORY - 1);
        List<String> keywordList = new ArrayList<>(
            Optional.of(searches).orElse(Collections.emptySet())
        );

        return new RecentSearchResponse(keywordList);
    }

    public RecentSearchResponse deleteSearchKeyword(final Accessor accessor, final String keyword) {
        String key = generateKey(accessor.getSocialId(), accessor.getUserRole().name());
        redisTemplate.opsForZSet().remove(key, keyword);
        return getRecentSearches(accessor);
    }

    public RecentSearchResponse deleteAllSearches(final Accessor accessor) {
        String key = generateKey(accessor.getSocialId(), accessor.getUserRole().name());
        redisTemplate.delete(key);
        return new RecentSearchResponse(Collections.emptyList());
    }

    private String generateKey(final String socialId, final String role) {
        return SEARCH_KEY_PREFIX + socialId + ":" + role;
    }
}
