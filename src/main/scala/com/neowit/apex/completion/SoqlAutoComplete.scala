package com.neowit.apex.completion

import com.neowit.apex.Session
import com.neowit.apex.parser.antlr.SoqlParser._
import com.neowit.apex.parser.antlr.{SoqlParser, SoqlLexer}
import com.neowit.apex.parser.{SoqlTreeListener, ApexParserUtils, ApexTree, Member}
import org.antlr.v4.runtime.tree.{ParseTree, ParseTreeWalker}
import org.antlr.v4.runtime.{ParserRuleContext, CommonTokenStream, ANTLRInputStream, Token}
import scala.collection.JavaConversions._

class SoqlCompletionResult(val options: List[Member], val isSoqlStatement: Boolean)

class SoqlAutoComplete (token: Token, line: Int, column: Int, cachedTree: ApexTree, session: Session) {

    def listOptions:SoqlCompletionResult = {

        val soqlString = stripBrackets(token.getText)
        val expressionTokens = getCaretStatement(soqlString)
        if (expressionTokens.isEmpty) {
            //looks like the file is too broken to get to the point where caret resides
            return new SoqlCompletionResult(List(), isSoqlStatement = true)
        }


        val input = new ANTLRInputStream(soqlString)
        val lexer = new SoqlLexer(input)
        val tokens = new CommonTokenStream(lexer)
        val parser = new SoqlParser(tokens)
        ApexParserUtils.removeConsoleErrorListener(parser)
        val tree = parser.soqlStatement()
        val walker = new ParseTreeWalker()

        Nil
    }

    /**
     * using Apex token and line/column of caret in currently edited file convert these to coordinates inside SOQL string
     * @param token - which contains SOQL expression [select ...]
     * @param line - original caret line num in the source file
     * @param column - original caret column in the source file
     * @return - index of caret in SOQL string
     */
    private def getCaretPositionInSoql(token: Token, line: Int, column: Int): (Int, Int) = {
        val caretLine = line - token.getLine
        val caretColumn = if (line == token.getLine ) {
            //SOQL starts in the middle of Apex line
            column - token.getCharPositionInLine
        } else {
            column
        }
        (caretLine, caretColumn)
    }

    /**
     * @param text - convert '[select ...]' into 'select ...'
     * @return
     */
    private def stripBrackets(text: String): String = {
        if (text.startsWith("[") && text.endsWith("]")) {
            text.substring(1, text.length - 1)
        } else {
            text
        }
    }
    /*
    private def getCaretStatement: List[AToken] = {
        val caret = new Caret(line, column, file)
        val tokenSource = new CodeCompletionTokenSource(getLexer(file), caret)
        val tokens: CommonTokenStream = new CommonTokenStream(tokenSource)  //Actual
        //val tokens: CommonTokenStream = new CommonTokenStream(getLexer(file))
        val parser = new ApexcodeParser(tokens)
        ApexParserUtils.removeConsoleErrorListener(parser)
        parser.setBuildParseTree(true)
        parser.setErrorHandler(new CompletionErrorStrategy())

        //parse tree until we reach caret caretAToken
        try {
            parser.compilationUnit()
        } catch {
            case ex: CaretReachedException =>
                //println("found caret?")
                //println(ex.getToken.getText)
                //listOptions(ex)
                return breakExpressionToATokens(ex)
            case e:Throwable =>
                println(e.getMessage)
        }

        List()

    }
    */

}
