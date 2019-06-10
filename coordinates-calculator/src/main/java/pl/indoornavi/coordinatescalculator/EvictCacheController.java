package pl.indoornavi.coordinatescalculator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
public class EvictCacheController {
    @Autowired
    public EvictCacheController(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    private final CacheManager cacheManager;

    @RequestMapping(path = "evictAll", method = RequestMethod.POST)
    public ResponseEntity evictCaches() {
        cacheManager.getCacheNames().forEach(name -> {
            Objects.requireNonNull(cacheManager.getCache(name)).clear();
        });
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
