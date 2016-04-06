package com.company;

import com.sun.tools.javac.util.Assert;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Main {


    public class CipherText {
        public  BigInteger gamma;
        public BigInteger sigma;

        public CipherText(BigInteger gamma, BigInteger sigma) {
            this.gamma = gamma;
            this.sigma = sigma;
        }
    }
    public class ElGamaPublicKey {
        public BigInteger p;
        public BigInteger g;
        public  BigInteger  ga;

        public CipherText encrypt(BigInteger plaintext) {
            /*
            1. Obtain A’s authentic public key (p, g, ga).
            2. Represent the message as integers m in the range
            {0, 1, . . . , p − 1}.
            3. Select a random integer k, 1 ≤ k ≤ p − 2.
            4. Compute γ = g^k mod p and δ = m ∗ (g^a)^k.
            5. Send ciphertext c = (γ, δ) to A*/
            if(!(plaintext.compareTo(p.subtract(BigInteger.ONE)) < 0)) {
                return null;
            }
            SecureRandom random = new SecureRandom();
            BigInteger bk;
            int k = 0;
            do {
                k = random.nextInt() % 10;
                if(k <0) {
                    k = -k;
                }
                bk = new BigInteger(k + "");
            }while(k  >0 && bk.compareTo(p.subtract(BigInteger.ONE.add(BigInteger.ONE))) > 0);

            BigInteger gamma = g.modPow(bk, p);
            BigInteger sigma = plaintext.multiply(ga).pow(k);
            CipherText cipherText = new CipherText(gamma,sigma);

            return cipherText;
        }

    }
    public class ElGamalPrivateKey {
        private ElGamaPublicKey publicKey;
        private BigInteger a;
        private BigInteger gamodp;

        public BigInteger decrypt(BigInteger ciphertext) {
            /*
            1. Use private key a to compute (γp−1−a) mod p.
            Note: γ^(p−1−a) = γ^−a = a^−ak
            2. Recover m by computing (γ−a) ∗ δ mod p
             */
            return null;
        }

        public ElGamaPublicKey getPublicKey() {
            return publicKey;
        }
        public  ElGamalPrivateKey generateKeyPair() {

            /*
            Key generation:
            1. Generate large prime p and generator g of the multiplicative
            Group Z∗p pf of the integers modulo p.

            2. Select a random integer a, 1 ≤ a ≤ p − 2, and compute
            g^a mod p.

            3. A’s Public key is (p, g, ga); A’s Private key is a
            */
            SecureRandom random = new SecureRandom();
            BigInteger p = new BigInteger(256,random).nextProbablePrime();
            BigInteger g = new BigInteger(256,random).nextProbablePrime();

            BigInteger a = BigInteger.ZERO;
            do {
                a = new BigInteger(256,random);
            }while(a.compareTo(BigInteger.ONE) >0 && a.compareTo(p.subtract(BigInteger.ONE.add(BigInteger.ONE))) < 0);

            this.a = a;
            this.gamodp = g.modPow(a,p);

            publicKey = new ElGamaPublicKey();
            publicKey.g = g;
            publicKey.p = p;
            publicKey.ga = g.multiply(a);
            return this;
        }
    }
    public void test1() {
        ElGamalPrivateKey privateKey = new ElGamalPrivateKey();
        privateKey.generateKeyPair();
        CipherText cipherText = privateKey.getPublicKey().encrypt(new BigInteger("99"));

    }
    public static void main(String[] args) {
	// write your code here
        Main main = new Main();
        main.test1();;

    }
}
