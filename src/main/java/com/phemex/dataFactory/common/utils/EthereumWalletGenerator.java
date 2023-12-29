package com.phemex.dataFactory.common.utils;

/**
 * @author: yuyu.shi
 * @Project: phemex
 * @Package: com.phemex.dataFactory.common.utils.GenETHAddress
 * @Date: 2023年07月10日 20:51
 * @Description:
 */

import org.web3j.crypto.ECKeyPair;
import org.web3j.crypto.Keys;

import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

/**
 * EthereumWalletGenerator类用于生成以太坊钱包地址和私钥。
 */
public class EthereumWalletGenerator {
    /**
     * 生成一个新的以太坊钱包地址。
     *
     * @return 生成的钱包地址
     * @throws InvalidAlgorithmParameterException 如果算法参数无效
     * @throws NoSuchAlgorithmException 如果指定的算法不可用
     * @throws NoSuchProviderException 如果指定的提供者不可用
     */
    public static String genWalletAddress() throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException {
        // 生成一个新的EC密钥对
        ECKeyPair ecKeyPair = Keys.createEcKeyPair();
        // 获取钱包地址
        StringBuilder walletAddress = new StringBuilder("0x");
        walletAddress.append(Keys.getAddress(ecKeyPair));
        // 获取私钥
        String privateKey = ecKeyPair.getPrivateKey().toString(16);
        return walletAddress.toString();
    }
}