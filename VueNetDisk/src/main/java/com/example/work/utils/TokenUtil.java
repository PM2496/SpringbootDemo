package com.example.work.utils;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.util.*;

public class TokenUtil {

    private static final long EXPIRE_TIME= 10*60*60*1000;
    private static final String TOKEN_SECRET="shit";  //密钥盐

    /**
     * 签名生成
     * @param
     * @return
     */
    public static String sign(String email, String username){
        String token = null;
        try {
            Date expiresAt = new Date(System.currentTimeMillis() + EXPIRE_TIME);
            token = JWT.create()
                    .withIssuer("auth0")
                    .withClaim("email", email)
                    .withClaim("username", username)
                    .withExpiresAt(expiresAt)
                    // 使用了HMAC256加密算法。
                    .sign(Algorithm.HMAC256(TOKEN_SECRET));
        } catch (Exception e){
            e.printStackTrace();
        }
        return token;
    }

    /**
     * 签名验证
     * @param token
     * @return
     */
    public static Map<String, String> verify(String token){
        try {
            JWTVerifier verifier = JWT.require(Algorithm.HMAC256(TOKEN_SECRET)).withIssuer("auth0").build();
            DecodedJWT jwt = verifier.verify(token);
            System.out.println("认证通过：");
            System.out.println("email: " + jwt.getClaim("email").asString() + " username: " + jwt.getClaim("username").asString());
            System.out.println("过期时间：      " + jwt.getExpiresAt());
            Map<String, String> map = new HashMap<>();
            map.put("email", jwt.getClaim("email").asString());
            map.put("username", jwt.getClaim("username").asString());
            return map;
        } catch (Exception e){
            return null;
        }
    }
}
