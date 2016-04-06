package com.company;

import com.sun.tools.javac.util.Assert;

import java.math.BigInteger;
import java.security.SecureRandom;

public class Main {

/*
I come up with two prime numbers g and p and tell you what they are.
You then pick a secret number (a), but you don't tell anyone. Instead you compute g^a mod p and send that result back to me. (We'll call that A since it came from a).
I do the same thing, but we'll call my secret number b and the computed number B. So I compute g^b mod p and send you the result (called "B")
Now, you take the number I sent you and do the exact same operation with it. So that's B^a mod p.
I do the same operation with the result you sent me, so: A^b mod p.

(g^a mod p)^b mod p = g^(ab) mod p ===> Bob takes Alice's A() exponent his secret modulo p
(g^b mod p)^a mod p = g^(ba) mod p ===> Alice takes Bob's B() exponent his secret modulo p
Of note, communicative property of exponentiation! pretty cool.  Thus we have a shared secret: (g^a mod p)^b mod p === (g^b mod p)^a mod p
*
*/

     class Alice {

         public BigInteger g;  // public
         public BigInteger p; // public for modulo
        private BigInteger a; // super secret

        public Alice(BigInteger g, BigInteger p) {
            SecureRandom secureRandom = new SecureRandom();
            this.g = g;
            this.p = p;
            this.a =  new BigInteger(256, secureRandom);
        }

        public BigInteger A() {


            return g.modPow(a,p);
        }
        public BigInteger sharedSecret(Bob bob) {
            return bob.B().modPow(a, p);
        }
    };
    class Bob{
        public BigInteger g;  // public
        public BigInteger p; // public for modulo
        private BigInteger b; // super secret

        public Bob(BigInteger g, BigInteger p) {
            SecureRandom secureRandom = new SecureRandom();
            this.g = g;
            this.p = p;
            this.b =  new BigInteger(256, secureRandom);
        }
        public BigInteger B()
        {
            SecureRandom secureRandom = new SecureRandom();

            this.b =  new BigInteger(256, secureRandom);

            return g.modPow(b,p);
        }

        public BigInteger sharedSecret(Alice alice) {
            return alice.A().modPow(b,p);
        }
    };

    public void  DH() {
        SecureRandom secureRandom = new SecureRandom();

        BigInteger g = new BigInteger(256, secureRandom).nextProbablePrime();
        BigInteger p = new BigInteger(256,secureRandom).nextProbablePrime();


        Alice alice = new Alice(g,p);
        Bob bob = new Bob(g,p);

        BigInteger sharedSecretAlice = alice.sharedSecret(bob);
        BigInteger sharedSecretBob = bob.sharedSecret(alice);

        Assert.check(sharedSecretAlice.equals(sharedSecretBob));
    }
    public static void main(String[] args) {
	// write your code here


        Main main = new Main();

        main.DH();

    }
}
