package lemon.jpizza.Libraries.Socks;

import lemon.jpizza.Contextuals.Context;
import lemon.jpizza.Errors.RTError;
import lemon.jpizza.Position;

import java.io.*;
import java.net.Socket;
import java.util.Arrays;

public class ClientConn extends GenConn {
    Socket client;

    double id;

    public ClientConn(double iydee, Position ps, Position pe, Context ctx) {
        id = iydee;
        pos_start = ps; pos_end = pe;
        context = ctx;
    }

    public RTError conn(String host, double port) {
        try {
            client = new Socket(host, (int) port);
            out = new DataOutputStream(client.getOutputStream());
            in = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
            return new RTError(
                    pos_start, pos_end,
                    "IOException while connecting..",
                    context
            );
        }
        return null;
    }

    public RTError close() {
        try {
            client.close();
        } catch (IOException e) {
            return new RTError(
                    pos_start, pos_end,
                    e.toString(),
                    context
            );
        } return null;
    }
}
