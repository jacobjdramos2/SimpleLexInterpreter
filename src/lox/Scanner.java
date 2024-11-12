package lox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static lox.TokenType.*;

public class Scanner {
    private final String source;
    private final List<lox.Token> tokens = new ArrayList<>();

    // The start and current fields are offsets that index into the string.
    // The start field points to the first character in the lexeme being scanned.
    private int start = 0;
    // The current points to the character currently being considered.
    private int current = 0;
    // The line field tracks what source line current is on, so we can produce tokens that know their location.
    private int line = 1;

    // To handle keywords, we see if the identifier's lexeme is one of the reserved words. If so,
    // we use a token type specific to that keyword. We define the set of reserved words in a map:
    private static final Map<String, TokenType> keywords;

    static {
        keywords = new HashMap<>();
        keywords.put("and",    AND);
        keywords.put("class",  CLASS);
        keywords.put("else",   ELSE);
        keywords.put("false",  FALSE);
        keywords.put("for",    FOR);
        keywords.put("run",    RUN);
        keywords.put("if",     IF);
        keywords.put("nil",    NIL);
        keywords.put("or",     OR);
        keywords.put("print",  PRINT);
        keywords.put("return", RETURN);
        keywords.put("super",  SUPER);
        keywords.put("this",   THIS);
        keywords.put("true",   TRUE);
        keywords.put("var",    VAR);
        keywords.put("while",  WHILE);

    }
    Scanner(String source) {
        this.source = source;
    }

    // We store the raw source code as a simple string, and we have a list ready to fill with tokens we're going to
    // generate.
    List<lox.Token> scanTokens() {
        while (!isAtEnd()) {
            // We are at the beginning of the next lexeme.
            start = current;
            scanToken();
        }

        tokens.add(new lox.Token(EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();
        switch(c) {
            case '(': addToken(LEFT_PAREN); break;
            case ')': addToken(RIGHT_PAREN); break;
            case '{': addToken(LEFT_BRACE); break;
            case '}': addToken(RIGHT_BRACE); break;
            case ',': addToken(COMMA); break;
            case '.': addToken(DOT); break;
            case '-': addToken(MINUS); break;
            case '+': addToken(PLUS); break;
            case ';': addToken(SEMICOLON); break;
            case '*': addToken(STAR); break;

            // If there is a second character for equality and comparison operators
            case '!': addToken(match('=') ? BANG_EQUAL : BANG); break;
            case '=': addToken(match('=') ? EQUAL_EQUAL : EQUAL); break;
            case '<': addToken(match('=') ? LESS_EQUAL : LESS); break;
            case '>': addToken(match('=') ? GREATER_EQUAL : GREATER); break;

            // Only for '/' since there are also comments too.
            case '/':
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else {
                    addToken(SLASH);
                }
                break;

            // When encountering whitespace and newlines, we go back to the beginning of the scan loop.
            // Newlines will also increment the line counter.
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n':
                line++;
                break;

            case '"': string(); break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    lox.Lox.error(line, "Unexpected character.");
                }
                break;
        }
    }

    // After scanning identifier, check for matches in the map
    private void identifier() {
        while (isAlphaNumeric(peek())) advance();

        String text = source.substring(start, current);
        TokenType type = keywords.get(text);
        if (type == null) type = IDENTIFIER;
        addToken(type);
    }

    private boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    private void number() {
        while (isDigit(peek())) advance();

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance();

            while (isDigit(peek())) advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    private boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') ||
                (c >= 'A' && c <= 'Z') ||
                c == '_';
    }

    private boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    // Like with comments we consume characters until we hit the " that ends the string.
    private void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            lox.Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance();

        //Trim the surrounding quotes.
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    // Basically a conditional advance(). We only consume the current character if it's what we're looking for.
    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;

        current++;
        return true;
    }

    // Similar to advance() except we don't consume the character.
    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    // Helper function to tell if we've consumed all the characters.
    private boolean isAtEnd() {
        return current >= source.length();
    }

    // Consumes the next character in the source file and returns it.
    private char advance() {
        current++;
        return source.charAt(current-1);
    }

    // addToken() grabs the text of the current lexeme and creates a new token for it.
    private void addToken(lox.TokenType type) {
        addToken(type, null);
    }

    private void addToken(lox.TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new lox.Token(type, text, literal, line));
    }
}
