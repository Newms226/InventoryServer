package edu.msudenver.mnewma12.server;

class ServerCli {

    public ServerCli() {}

    public void echoSuccess(UDPResponse response) {
        System.out.println("OUT" + response + " [" + Thread.currentThread() + "]");
    }

    public void echoFailure(Throwable throwable) {
        throwable.printStackTrace();
    }
}
