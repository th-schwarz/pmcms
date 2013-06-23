/*******************************************************************************
 * Poor Man's CMS (pmcms) - A very basic CMS generating static html pages.
 * http://poormans.sourceforge.net
 * Copyright (C) 2004-2013 by Thilo Schwarz
 * 
 * == BEGIN LICENSE ==
 * 
 * Licensed under the terms of any of the following licenses at your
 * choice:
 * 
 *  - GNU Lesser General Public License Version 2.1 or later (the "LGPL")
 *    http://www.gnu.org/licenses/lgpl.html
 * 
 *  - Mozilla Public License Version 1.1 or later (the "MPL")
 *    http://www.mozilla.org/MPL/MPL-1.1.html
 * 
 * == END LICENSE ==
 ******************************************************************************/
package de.thischwa.pmcms.tool;

import java.io.UnsupportedEncodingException;
import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

/**
 * Simple utility object to encrypt and decrypt text using the DES algorithm. Encoding is UTF8.
 * 
 * @author Thilo Schwarz
 */
public class DESCryptor {
	private static final String encoding = "UTF8";
	private String algorithm = "DES";

	private String secretKeyPlain;
	
	private Key key;

	public DESCryptor(String secretKeyPlain) {
		this.secretKeyPlain = secretKeyPlain;
	}

	public String encrypt(String plainTxt) throws CryptorException {
		if(plainTxt == null || plainTxt.trim().length() == 0)
			return null;
		if(key == null)
			key = buildKey();
		try {
			byte[] cleartext = plainTxt.getBytes(encoding);
			Cipher cipher = Cipher.getInstance(algorithm); // cipher is not thread safe
			cipher.init(Cipher.ENCRYPT_MODE, key);
			String encrypedPwd = Base64.encodeBase64String(cipher.doFinal(cleartext));
			return encrypedPwd;
		} catch (Exception e) {
			throw new CryptorException(e);
		}
	}

	public String decrypt(String encryptedTxt) throws CryptorException {
		if(encryptedTxt == null || encryptedTxt.trim().length() == 0)
			return null;
		if(key == null)
			key = buildKey();
		try {
			byte[] encrypedPwdBytes = Base64.decodeBase64(encryptedTxt);
			Cipher cipher = Cipher.getInstance(algorithm); // cipher is not thread safe
			cipher.init(Cipher.DECRYPT_MODE, key);
			byte[] plainTextPwdBytes = cipher.doFinal(encrypedPwdBytes);
			return new String(plainTextPwdBytes);
		} catch (Exception e) {
			throw new CryptorException(e);
		}
	}

	private Key buildKey() throws CryptorException {
		try {
			return new SecretKeySpec(secretKeyPlain.getBytes(encoding), this.algorithm);
		} catch (UnsupportedEncodingException e) {
			throw new CryptorException(e);
		}
	}
	
	public class CryptorException extends RuntimeException {
		private static final long serialVersionUID = 1L;

		public CryptorException(Throwable cause) {
			super(cause);
		}
	}
}