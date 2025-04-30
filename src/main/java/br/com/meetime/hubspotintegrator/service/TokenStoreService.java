package br.com.meetime.hubspotintegrator.service;

import br.com.meetime.hubspotintegrator.dto.response.TokenResponseDto;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class TokenStoreService {

    private final ConcurrentHashMap<String, TokenResponseDto> tokenMap = new ConcurrentHashMap<>();

    public void store(String key, TokenResponseDto token) {
        tokenMap.put(key, token);
    }

    public TokenResponseDto get(String key) {
        return tokenMap.get(key);
    }
}