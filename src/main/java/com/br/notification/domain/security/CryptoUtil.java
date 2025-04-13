package com.br.notification.domain.security;

import com.br.notification.domain.exception.CryptoException;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@NoArgsConstructor
public class CryptoUtil {

  @Value("${encryption.secret}")
  private String secret;

  @Value("${encryption.algorithm}")
  private String algorithm;

  @Value("${encryption.secret.algorithm}")
  private String secretAlgorithm;

  private SecretKeySpec getSecretKey() {
    return new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), secretAlgorithm);
  }

  public String decrypt(String encryptedText) {
    try {
      byte[] decodedData = Base64.getDecoder().decode(encryptedText);

      SecretKeySpec key = getSecretKey();

      Cipher cipher = Cipher.getInstance(algorithm);
      cipher.init(Cipher.DECRYPT_MODE, key);

      byte[] originalData = cipher.doFinal(decodedData);

      return new String(originalData, StandardCharsets.UTF_8);
    } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
      throw new CryptoException("Cryptographic algorithm error: " + e.getMessage(), e);
    } catch (IllegalBlockSizeException | BadPaddingException e) {
      throw new CryptoException("Block size or padding error: " + e.getMessage(), e);
    } catch (InvalidKeyException e) {
      throw new CryptoException("Key or algorithm parameter error: " + e.getMessage(), e);
    } catch (Exception e) {
      throw new CryptoException("Unknown error during decryption: " + e.getMessage(), e);
    }
  }
}
