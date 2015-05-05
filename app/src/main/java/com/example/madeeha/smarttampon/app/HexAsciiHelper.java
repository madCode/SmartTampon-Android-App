package com.example.madeeha.smarttampon.app;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.apache.http.util.ByteArrayBuffer;

import java.util.regex.Pattern;

public class HexAsciiHelper {
    public static int PRINTABLE_ASCII_MIN = 0x20; // ' '
    public static int PRINTABLE_ASCII_MAX = 0x7E; // '~'

    public static boolean isPrintableAscii(int c) {
        return c >= PRINTABLE_ASCII_MIN && c <= PRINTABLE_ASCII_MAX;
    }

    public static String bytesToHex(byte[] data) {
        return bytesToHex(data, 0, data.length);
    }

    public static String bytesToHex(byte[] data, int offset, int length) {
        if (length <= 0) {
            return "";
        }

        StringBuilder hex = new StringBuilder();
        for (int i = offset; i < offset + length; i++) {
            hex.append(String.format(" %02X", data[i] % 0xFF));
        }
        hex.deleteCharAt(0);
        return hex.toString();
    }

    public static String bytesToAsciiMaybe(byte[] data) {
        return bytesToAsciiMaybe(data, 0, data.length);
    }

    public static String bytesToAsciiMaybe(byte[] data, int offset, int length) {
        StringBuilder ascii = new StringBuilder();
        boolean zeros = false;
        for (int i = offset; i < offset + length; i++) {
            int c = data[i] & 0xFF;
            if (isPrintableAscii(c)) {
                if (zeros) {
                    return null;
                }
                ascii.append((char) c);
            } else if (c == 0) {
                zeros = true;
            } else {
                return null;
            }
        }
        return ascii.toString();
    }

    public static byte[] hexToBytes(String hex) {
        ByteArrayBuffer bytes = new ByteArrayBuffer(hex.length() / 2);
        for (int i = 0; i < hex.length(); i++) {
            if (hex.charAt(i) == ' ') {
                continue;
            }

            String hexByte;
            if (i + 1 < hex.length()) {
                hexByte = hex.substring(i, i + 2).trim();
                i++;
            } else {
                hexByte = hex.substring(i, i + 1);
            }

            bytes.append(Integer.parseInt(hexByte, 16));
        }
        return bytes.buffer();
    }

    public static int hexToFloat(String hex){
        int h=hex.length()-1;int d=0;int n=0;
        for(int i=0;i<hex.length();i++)
        {
            char ch=hex.charAt(i);boolean flag=false;

            switch(ch)
            {
                case '1':n=1;break;
                case '2':n=2;break;
                case '3':n=3;break;
                case '4':n=4;break;
                case '5':n=5;break;
                case '6':n=6;break;
                case '7':n=7;break;
                case '8':n=8;break;
                case '9':n=9;break;
                case 'A':n=10;break;
                case 'B':n=11;break;
                case 'C':n=12;break;
                case 'D':n=13;break;
                case 'E':n=14;break;
                case 'F':n=15;break;
                default : flag=true;
            }
            if(flag){System.out.println("Wrong Entry");break;}
            d=(int)(n*(Math.pow(16,h)))+(int)d;
            h--;

        }
        return d;
    }
}
