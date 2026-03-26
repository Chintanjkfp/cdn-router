package com.cdn.cdnrouter.router;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;


import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;


@RestController
@RequestMapping("/cdn")
public class RouterController {

    private final List<String> origins;
    private final AtomicInteger counter = new AtomicInteger(0);
    private final RestTemplate restTemplate = new RestTemplate();

    public RouterController(@Value("${cdn.origins}") String originsCsv) {
        this.origins = Arrays.stream(originsCsv.split(","))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .toList();
    }

    @GetMapping("/{key}")
    public ResponseEntity<byte[]> route(
            @PathVariable String key,
            @RequestHeader(value = "Range", required = false) String rangeHeader
    ) {
        int idx = Math.floorMod(counter.getAndIncrement(), origins.size());
        String target = origins.get(idx);
        System.out.println("[ROUTER] key=" + key + " -> " + target);

        String url = target + "/stream/" + key;

        HttpHeaders headers = new HttpHeaders();
        if (rangeHeader != null) {
            headers.set("Range", rangeHeader);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<byte[]> originResp =
                restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

        HttpHeaders out = new HttpHeaders();
        out.putAll(originResp.getHeaders());

        return new ResponseEntity<>(originResp.getBody(), out, originResp.getStatusCode());
    }
    @GetMapping("/whoami")
    public String testWhoAmI() {
        // 1. Pick the next origin (Round Robin)
        int idx = Math.floorMod(counter.getAndIncrement(), origins.size());
        String target = origins.get(idx);

        System.out.println("[ROUTER] Testing WhoAmI -> " + target);

        // 2. Call the origin directly (NO /stream/ prefix)
        return restTemplate.getForObject(target + "/whoami", String.class);
    }

    // New method for GitHub-backed streaming
    @GetMapping("/git/{key}")
    public ResponseEntity<byte[]> routeGit(
            @PathVariable String key,
            @RequestHeader(value = "Range", required = false) String rangeHeader
    ) {
        int idx = Math.floorMod(counter.getAndIncrement(), origins.size());
        String target = origins.get(idx);

        // This calls the GitHub method in your Origins
        String url = target + "/streams/" + key;

        System.out.println("[ROUTER-GIT] Routing to GitHub -> " + url);

        HttpHeaders headers = new HttpHeaders();
        if (rangeHeader != null) {
            headers.set("Range", rangeHeader);
        }

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // Forward the request to the Origin's /streams/ endpoint
        ResponseEntity<byte[]> originResp =
                restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);

        HttpHeaders out = new HttpHeaders();
        out.putAll(originResp.getHeaders());

        return new ResponseEntity<>(originResp.getBody(), out, originResp.getStatusCode());
    }
}