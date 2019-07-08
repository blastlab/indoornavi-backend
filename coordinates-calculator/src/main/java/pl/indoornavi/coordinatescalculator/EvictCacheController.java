package pl.indoornavi.coordinatescalculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController("/")
public class EvictCacheController {
    @Autowired
    public EvictCacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private final CacheManager cacheManager;

    @RequestMapping(path = "clearCache", method = RequestMethod.POST)
    public ResponseEntity evictCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            Cache cache = cacheManager.getCache(name);
            if (cache != null) {
                cache.clear();
            }
        });
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
