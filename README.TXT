Notes
Scanning - The scanner takes in raw source code as a series of characters and groups it into a series of chunks we
call tokens. These are the meaningful "words" and "punctuation" that make up the language's grammar.

Lexical Analysis
var language = "lox";
Here, var is the keyword for declaring a variable. That three-character sequence "v-a-r" means something. But if we
yank three letters out of the middle language, like "g-u-a", those don't mean anything on their own.

Our job is to scan through the list of character and group them together into the smallest sequences that still
represent something.

Each part of this statement is called a lexeme: var language = "lox"; They are the only raw substrings of the
source code. When we take the lexeme and bundle it together with that other data, the result is a token.

Lexical grammar - The rules that determine how a particular language groups characters into lexemes.

The rules of that grammar are simple enough to be classified a regular language.

What s Lox's Lexical Grammar - Set of rules that define how to break down sequences of characters into meaningful
tokens (like keywords, operators, identifiers, etc) for the Lox language. This is the first step in the parsing process,
where the interpreter or compiler identifies the smallest units of meaning (lexemes) in the code.
