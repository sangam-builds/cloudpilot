package com.cloudpilot.dto;

public class AuthResponseDto {

    private String accessToken;
    private String refreshToken;
    private String tokenType;
    private Long expiresIn;
    private Long userId;
    private String email;
    private String name;
    private String role;

    public AuthResponseDto() {}

    public AuthResponseDto(String accessToken, String refreshToken, String tokenType, Long expiresIn, Long userId, String email, String name, String role) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.userId = userId;
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public static AuthResponseDtoBuilder builder() { return new AuthResponseDtoBuilder(); }

    public static class AuthResponseDtoBuilder {
        private String accessToken;
        private String refreshToken;
        private String tokenType;
        private Long expiresIn;
        private Long userId;
        private String email;
        private String name;
        private String role;

        public AuthResponseDtoBuilder accessToken(String accessToken) { this.accessToken = accessToken; return this; }
        public AuthResponseDtoBuilder refreshToken(String refreshToken) { this.refreshToken = refreshToken; return this; }
        public AuthResponseDtoBuilder tokenType(String tokenType) { this.tokenType = tokenType; return this; }
        public AuthResponseDtoBuilder expiresIn(Long expiresIn) { this.expiresIn = expiresIn; return this; }
        public AuthResponseDtoBuilder userId(Long userId) { this.userId = userId; return this; }
        public AuthResponseDtoBuilder email(String email) { this.email = email; return this; }
        public AuthResponseDtoBuilder name(String name) { this.name = name; return this; }
        public AuthResponseDtoBuilder role(String role) { this.role = role; return this; }

        public AuthResponseDto build() {
            return new AuthResponseDto(accessToken, refreshToken, tokenType, expiresIn, userId, email, name, role);
        }
    }

    public String getAccessToken() { return accessToken; }
    public void setAccessToken(String accessToken) { this.accessToken = accessToken; }
    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }
    public Long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(Long expiresIn) { this.expiresIn = expiresIn; }
    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }
}
