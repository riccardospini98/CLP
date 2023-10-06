// Generated from C:/Users/gabri/Desktop/CLP/CLP/progettoANTLR/src/parser/SVM.g4 by ANTLR 4.13.1
package SVM;

import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link SVMParser}.
 */
public interface SVMListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link SVMParser#assembly}.
	 * @param ctx the parse tree
	 */
	void enterAssembly(SVMParser.AssemblyContext ctx);
	/**
	 * Exit a parse tree produced by {@link SVMParser#assembly}.
	 * @param ctx the parse tree
	 */
	void exitAssembly(SVMParser.AssemblyContext ctx);
	/**
	 * Enter a parse tree produced by {@link SVMParser#instruction}.
	 * @param ctx the parse tree
	 */
	void enterInstruction(SVMParser.InstructionContext ctx);
	/**
	 * Exit a parse tree produced by {@link SVMParser#instruction}.
	 * @param ctx the parse tree
	 */
	void exitInstruction(SVMParser.InstructionContext ctx);
}