const TokenType = Object.freeze({
  IDENTIFIER: "IDENTIFIER",
  KEYWORD: "KEYWORD",
  NUMBER: "NUMBER",
  STRING: "STRING",
  SPACE: "SPACE",
  NEWLINE: "NEWLINE",
  TAB: "TAB",
  IMPORTNAME: "IMPORTNAME",
  HEADDATATYPE: "HEADDATATYPE",
  CONSTANT: "CONSTANT",
  OTHERPUNCTUATION: "OTHERPUNCTUATION",
  JAVADOC: "JAVADOC",
  ANNOTATION: "ANNOTATION",
});

class Parser {
  constructor(tokens) {
    this.tokens = tokens;
    this.outCodeHTML = "";
    this.outPlain = "";
  }

  parse() {
    for (let index = 0; index < this.tokens.length; index++) {
      switch (String(this.tokens[index].type)) {
        case "KEYWORD":
        case "IDENTIFIER":
        case "NUMBER":
        case "STRING":
        case "IMPORTNAME":
        case "HEADDATATYPE":
        case "OTHERPUNCTUATION":
        case "JAVADOC":
        case "ANNOTATION":
        case "CONSTANT":
          this.outCodeHTML +=
            '<span class="' +
            String(this.tokens[index].type).toLowerCase() +
            '">' +
            this.cleanse(this.tokens[index].text) +
            "</span>";
          this.outPlain += this.tokens[index].text;
          break;
        case "NEWLINE":
          this.outCodeHTML +=
            '<span class="' +
            String(this.tokens[index].type).toLowerCase() +
            '"><br /></span>';
          this.outPlain += "\n";
          break;
        case "TAB":
          this.outCodeHTML +=
            '<span class="' +
            String(this.tokens[index].type).toLowerCase() +
            '">    </span>';
          this.outPlain += "\t";
          break;
        case "SPACE":
          this.outCodeHTML +=
            '<span class="' +
            String(this.tokens[index].type).toLowerCase() +
            '"> </span>';
          this.outPlain += " ";
          break;
      }
    }
    return this.outCodeHTML;
  }

  cleanse(text) {
    return text
      .replaceAll("[&]", "&amp;")
      .replaceAll("[ ]", "&nbsp;")
      .replaceAll("[{]", "&lbrace;")
      .replaceAll("[<]", "&lt;")
      .replaceAll("[>]", "&gt;");
  }
}

class Token {
  constructor(text, line, col, type) {
    this.text = text;
    this.line = line;
    this.col = col;
    this.type = type;
  }
}

class Tokenizer {
  constructor() {
    this.line = 1;
    this.col = 0;
    this.start = 0;
    this.current = 0;
    this.tokens = [];
    this.sourceCode = "";
    this.keywords = [];
    this.keywords.push("abstract");
    this.keywords.push("assert");
    this.keywords.push("boolean");
    this.keywords.push("break");
    this.keywords.push("byte");
    this.keywords.push("case");
    this.keywords.push("catch");
    this.keywords.push("char");
    this.keywords.push("class");
    this.keywords.push("continue");
    this.keywords.push("const");
    this.keywords.push("default");
    this.keywords.push("do");
    this.keywords.push("double");
    this.keywords.push("else");
    this.keywords.push("enum");
    this.keywords.push("exports");
    this.keywords.push("extends");
    this.keywords.push("final");
    this.keywords.push("finally");
    this.keywords.push("float");
    this.keywords.push("for");
    this.keywords.push("goto");
    this.keywords.push("if");
    this.keywords.push("implements");
    this.keywords.push("import");
    this.keywords.push("instanceof");
    this.keywords.push("int");
    this.keywords.push("interface");
    this.keywords.push("long");
    this.keywords.push("module");
    this.keywords.push("native");
    this.keywords.push("new");
    this.keywords.push("non-sealed");
    this.keywords.push("package");
    this.keywords.push("private");
    this.keywords.push("protected");
    this.keywords.push("public");
    this.keywords.push("requires");
    this.keywords.push("return");
    this.keywords.push("short");
    this.keywords.push("static");
    this.keywords.push("strictfp");
    this.keywords.push("super");
    this.keywords.push("switch");
    this.keywords.push("synchronized");
    this.keywords.push("this");
    this.keywords.push("throw");
    this.keywords.push("throws");
    this.keywords.push("transient");
    this.keywords.push("try");
    this.keywords.push("var");
    this.keywords.push("void");
    this.keywords.push("volatile");
    this.keywords.push("while");
    this.keywords.push("yield");
    this.keywords.push("sealed");
    this.keywords.push("record");
    this.keywords.push("permits");

    // literals
    this.keywords.push("true");
    this.keywords.push("false");
    this.keywords.push("null");
    this.keywords.push("System");
  }

  peek() {
    return this.sourceCode.charAt(this.current + 1);
  }

  isAlphabet(currentChar) {
    return (
      String(currentChar).match(new RegExp("[a-zA-Z_$]")) !=
      null
    );
  }

  isIdentifierEnding(currentChar) {
    return (
      String(currentChar).match(new RegExp("[a-zA-Z_$0-9]")) !=
      null
    );
  }

  isAtEnd() {
    return this.current >= this.sourceCode.length;
  }

  annotation() {
    while (!this.isAtEnd()) {
      if (this.peek() == " ") {
        break;
      } else if (this.peek() == "\n") {
        this.line++;
        break;
      }
      this.col++;
      this.current++;
    }
  }

  multilineString() {
    while (!this.isAtEnd()) {
      if (
        this.sourceCode.charAt(this.current - 1) != "\\" &&
        this.sourceCode.charAt(this.current) == '"' &&
        this.peek() == '"' &&
        this.peekNext() == '"'
      ) {
        break;
      } else if (this.peek() == "\n") {
        this.line++;
      }
      this.col++;
      this.current++;
    }
  }

  multilineComment() {
    while (!this.isAtEnd()) {
      if (this.peek() == "*" && this.peekNext() == "/") {
        break;
      } else if (this.peek() == "\n") {
        this.line++;
      }
      this.col++;
      this.current++;
    }
    this.col++;
    this.current++;
  }

  peekNext() {
    return this.sourceCode.charAt(this.current + 2);
  }

  isUppercase(text) {
    return text.match(new RegExp("^[A-Z_$][A-Z_$0-9]*$")) != null;
  }

  comment() {
    while (!this.isAtEnd() && this.peek() != "\n") {
      this.col++;
      this.current++;
    }
  }

  character() {
    while (
      !this.isAtEnd() &&
      (this.peek() != "'" || this.sourceCode.charAt(this.current) == "\\")
    ) {
      this.col++;
      this.current++;
    }
  }

  string() {
    while (
      !this.isAtEnd() &&
      (this.peek() != '"' || this.sourceCode.charAt(this.current) == "\\")
    ) {
      this.col++;
      this.current++;
    }
  }

  isNumerical(currentChar) {
    return String(currentChar).match(new RegExp("[0-9]")) != null;
  }

  tokenize(code) {
    this.sourceCode = code;
    while (!this.isAtEnd()) {
      var currentChar = this.sourceCode.charAt(this.current);
      if (this.isAlphabet(currentChar)) {
        while (!this.isAtEnd() && this.isIdentifierEnding(this.peek())) {
          this.col++;
          this.current++;
        }
        if (
          this.keywords.includes(code.substring(this.start, this.current + 1))
        ) {
          this.tokens.push(
            new Token(
              code.substring(this.start, this.current + 1),
              this.line,
              this.col,
              TokenType.KEYWORD
            )
          );
        } else {
          this.tokens.push(
            new Token(
              code.substring(this.start, this.current + 1),
              this.line,
              this.col,
              TokenType.IDENTIFIER
            )
          );
        }
        this.col++;
        this.current++;
        this.start = this.current;
      } else {
        if (this.isNumerical(currentChar)) {
          while (!this.isAtEnd()) {
            if (this.isNumerical(this.peek()) || this.peek() == ".") {
              this.col++;
              this.current++;
            } else {
              break;
            }
          }

          this.tokens.push(
            new Token(
              code.substring(this.start, this.current + 1),
              this.line,
              this.col,
              TokenType.NUMBER
            )
          );
        } else {
          switch (currentChar) {
            case " ":
              this.tokens.push(
                new Token(
                  " ",
                  this.line,
                  this.col,
                  TokenType.SPACE
                )
              );
              break;
            case "\n":
              this.tokens.push(
                new Token(
                  "\n",
                  this.line,
                  this.col,
                  TokenType.NEWLINE
                )
              );
              this.line++;
              this.col = -1;
              break;
            case "\t":
              this.tokens.push(
                new Token(
                  "    ",
                  this.line,
                  this.col,
                  TokenType.TAB
                )
              );
              break;
            case "@":
              this.annotation();
              this.current++;
              this.tokens.push(
                new Token(
                  code.substring(this.start, this.current + 1),
                  this.line,
                  this.col,
                  TokenType.ANNOTATION
                )
              );
              break;
            case '"':
              // Supports multiline strings
              if (this.peek() == '"' && this.peekNext() == '"') {
                this.multilineString();
                this.current++;
                this.tokens.push(
                  new Token(
                    code.substring(this.start, this.current + 1),
                    this.line,
                    this.col,
                    TokenType.STRING
                  )
                );
              } else {
                this.string();
                this.current++;
                this.tokens.push(
                  new Token(
                    code.substring(this.start, this.current + 1),
                    this.line,
                    this.col,
                    TokenType.STRING
                  )
                );
              }
              break;
            case "'":
              this.character();
              this.current++;
              this.tokens.push(
                new Token(
                  code.substring(this.start, this.current + 1),
                  this.line,
                  this.col,
                  TokenType.STRING
                )
              );
              break;
            case "/":
              if (this.peek() == "/") {
                this.comment();
                this.current++;
                this.tokens.push(
                  new Token(
                    code.substring(this.start, this.current + 1),
                    this.line,
                    this.col,
                    TokenType.STRING
                  )
                );
              } else if (this.peek() == "*") {
                if (this.peekNext() != "*") {
                  this.multilineComment();
                  this.current++;
                  this.tokens.push(
                    new Token(
                      code.substring(this.start, this.current + 1),
                      this.line,
                      this.col,
                      TokenType.STRING
                    )
                  );
                } else {
                  this.multilineComment();
                  this.current++;
                  this.tokens.push(
                    new Token(
                      code.substring(this.start, this.current + 1),
                      this.line,
                      this.col,
                      TokenType.JAVADOC
                    )
                  );
                }
              } else {
                this.tokens.push(
                  new Token(
                    code.substring(this.start, this.current + 1),
                    this.line,
                    this.col,
                    TokenType.OTHERPUNCTUATION
                  )
                );
              }
              break;
            case ";":
            case "(":
            case ")":
            case "{":
            case "}":
            case "<":
            case ">":
            case "[":
            case "]":
            case ",":
            case "*":
            case "=":
            case "+":
            case "-":
            case "%":
            case "!":
            case "~":
            case "&":
            case "|":
            case "?":
            case ":":
            case "^":
            case ".":
              this.tokens.push(
                new Token(
                  code.substring(this.start, this.current + 1),
                  this.line,
                  this.col,
                  TokenType.OTHERPUNCTUATION
                )
              );
              break;
            default:
              console.log(
                "Could not interpret character: '" +
                  currentChar +
                  "'. [ln: " +
                  this.line +
                  "]"
              );
              break;
          }
        }
        this.col++;
        this.current++;
        this.start = this.current;
      }
    }

    for (var index = 0; index < this.tokens.length; index++) {
      if (
        this.tokens[index].text === "import" ||
        this.tokens[index].text === "package"
      ) {
        var indexstart = index + 1;
        while (this.tokens[indexstart].text !== ";") {
          var token = this.tokens[indexstart];
          this.tokens.splice(
            indexstart,
            0,
            new Token(
              token.text,
              token.line,
              token.col,
              TokenType.IMPORTNAME
            )
          );
          this.tokens.splice(indexstart + 1, 1);
          indexstart++;
        }
      } else if (
        this.tokens[index].type == TokenType.IDENTIFIER ||
        (this.tokens[index].text.match(/[})\]]/g) != null)
      ) {
        this.current = index;
        if (!this.isAtEnd() && this.tokens.length > index + 1 && this.tokens[index + 1].text === ".") {
          var indexstart = index + 1;
          while (
            this.tokens[indexstart].type != TokenType.OTHERPUNCTUATION ||
            this.tokens[indexstart].text === "."
          ) {
            if (this.tokens[indexstart].text === ".") {
              indexstart++;
              continue;
            }
            var token = this.tokens[indexstart];
            this.tokens.splice(
              indexstart,
              0,
              new Token(
                token.text,
                token.line,
                token.col,
                TokenType.HEADDATATYPE
              )
            );
            this.tokens.splice(indexstart + 1, 1);
            indexstart++;
          }
        }
      }

      if (
        this.isUppercase(this.tokens[index].text) &&
        (this.tokens[index].type == TokenType.HEADDATATYPE ||
          this.tokens[index].type == TokenType.IDENTIFIER)
      ) {
        var token = this.tokens[index];
        this.tokens.splice(
          index,
          0,
          new Token(token.text, token.line, token.col, TokenType.CONSTANT)
        );
        this.tokens.splice(index + 1, 1);
      }
    }

    return this.tokens;
  }
}

var code = document.getElementsByTagName("code");
for (var i = 0; i < code.length; i++) {
  var tokensList = new Tokenizer().tokenize(code[i].innerText);
  var parser = new Parser(tokensList);
  var output = parser.parse();
  code[i].innerHTML = output;
}
