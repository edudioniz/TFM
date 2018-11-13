package es.ediaz.core.object;

public class Token {
    private String token; 
    private Token(String token){
        this.token = token;
    }
    public Token init(String token){
        return new Token(token);
    }
    public String getToken(){
        return token;
    }
}
