package com.br.notification.domain.security;

import static org.junit.jupiter.api.Assertions.*;

import com.br.notification.domain.exception.CryptoException;
import java.lang.reflect.Field;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CryptoUtilTest {

  @InjectMocks private CryptoUtil cryptoUtil;

  @Mock private Cipher cipher;

  @Mock private SecretKeySpec secretKeySpec;

  @BeforeEach
  void setUp() throws NoSuchFieldException, IllegalAccessException {
    MockitoAnnotations.openMocks(this);
    Field secretField = CryptoUtil.class.getDeclaredField("secret");
    secretField.setAccessible(true);
    String secret = "mySecretKey12345";
    secretField.set(cryptoUtil, secret);

    Field algorithmField = CryptoUtil.class.getDeclaredField("algorithm");
    algorithmField.setAccessible(true);
    String algorithm = "AES/ECB/ISO10126Padding";
    algorithmField.set(cryptoUtil, algorithm);

    Field secretAlgorithmField = CryptoUtil.class.getDeclaredField("secretAlgorithm");
    secretAlgorithmField.setAccessible(true);
    String secretAlgorithm = "AES";
    secretAlgorithmField.set(cryptoUtil, secretAlgorithm);
  }

  @Test
  void testDecryptSuccessfully() {
    String encryptedText = "LLrXxXCELPgN8T/LRyOJtw==";

    String decryptedText = cryptoUtil.decrypt(encryptedText);

    assertEquals("plainText", decryptedText);
  }

  @Test
  void testDecryptThrowsCryptoExceptionWhenIllegalBlockSizeException() {
    String encryptedText = "KflsRhzmhErL";

    CryptoException exception =
        assertThrows(CryptoException.class, () -> cryptoUtil.decrypt(encryptedText));
    assertTrue(exception.getMessage().contains("Block size or padding error"));
  }

  @Test
  void testDecryptThrowsCryptoExceptionWhenBadPaddingException() {
    String encryptedText = "AdKRIenk3FU0EEajrvZUkw==";

    CryptoException exception =
        assertThrows(CryptoException.class, () -> cryptoUtil.decrypt(encryptedText));
    assertTrue(exception.getMessage().contains("Block size or padding error"));
  }

  @Test
  void testDecryptThrowsCryptoExceptionWhenNoSuchAlgorithmExceptionException() throws Exception {
    String encryptedText = Base64.getEncoder().encodeToString("plainText".getBytes());
    Field algorithmField = CryptoUtil.class.getDeclaredField("algorithm");
    algorithmField.setAccessible(true);
    String algorithm = "AES/ECB/ISO10121Padding";
    algorithmField.set(cryptoUtil, algorithm);

    CryptoException exception =
        assertThrows(CryptoException.class, () -> cryptoUtil.decrypt(encryptedText));
    assertTrue(exception.getMessage().contains("Cryptographic algorithm error"));
  }
}
