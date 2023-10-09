import ast.*;
import ast.Types.BoolType;
import ast.Types.IntType;
import ast.Types.Type;
import ast.Types.VoidType;
import parser.*;

import java.util.ArrayList;

public class Visitor extends SimpLanPlusBaseVisitor<Node>{
    /**
     * prog: exp
    **/
    @Override
    public Node visitSingleExpProg(SimpLanPlusParser.SingleExpProgContext ctx) {
        return new ProgNode(visit(ctx.exp()));
    }
    /**
     * prog: (dec)+ (stm)* (exp)?
    **/
    @Override
    public Node visitRegularProg(SimpLanPlusParser.RegularProgContext ctx) {
        ArrayList<Node> declarations = new ArrayList<>();
        ArrayList<Node> statements = new ArrayList<>();
        for (SimpLanPlusParser.DecContext dec: ctx.dec()) {
            declarations.add((visit(dec)));
        }
        for (SimpLanPlusParser.StmContext stm: ctx.stm()) {
            statements.add((visit(stm)));
        }
        if(ctx.exp()!=null) {
            Node exp = visit(ctx.exp());
            return new ProgLetInNode(declarations, statements, exp);
        } else {
            return new ProgLetInNode(declarations, statements);
        }
    }

    /**
     * dec: type ID ';'
    **/
    @Override
    public Node visitVarDec(SimpLanPlusParser.VarDecContext ctx) {
        return new DecNode(ctx.ID().getText(), (Type) visit( ctx.type() ));
    }

    /**
     * dec: type ID '(' (param (',' param)*)? ')' '{' body '}'
     **/
    @Override
    public Node visitFunDec(SimpLanPlusParser.FunDecContext ctx) {
        ArrayList<ParNode> params = new ArrayList<>();
        for (SimpLanPlusParser.ParamContext p: ctx.param()) {
            params.add(new ParNode(p.ID().getText(), (Type) visit(p.type())));
        }
        Node body = visit(ctx.body());
        return new FunNode(ctx.ID().getText(), (Type)visit(ctx.type()), params, body);
    }

    /**
     * parm: type ID
     **/
    @Override
    public Node visitParam(SimpLanPlusParser.ParamContext ctx) {
        return new ParNode(ctx.ID().getText(), (Type) visit( ctx.type() ));
    }

    /**
     * body: TODO
     **/
    @Override
    public Node visitBody(SimpLanPlusParser.BodyContext ctx) {
        ArrayList<Node> declarations = new ArrayList<>();
        ArrayList<Node> statements = new ArrayList<>();

        for (SimpLanPlusParser.DecContext d : ctx.dec()){
            declarations.add(visit(d));
        }
        for (SimpLanPlusParser.StmContext s : ctx.stm()){
            statements.add(visit(s));
        }

        if (ctx.exp()!=null) {
            return new BodyNode(declarations, statements, visit(ctx.exp()));
        }
        return new BodyNode(declarations, statements);
    }

    /**
     * type: 'int' | 'bool' | 'void'
     **/
    @Override
    public Node visitType(SimpLanPlusParser.TypeContext ctx) {
        return switch (ctx.getText()) {
            case "int" -> new IntType();
            case "bool" -> new BoolType();
            default -> new VoidType();
        };
    }

    /**
     * ID '=' exp ';'
     **/
    @Override
    public Node visitAssign(SimpLanPlusParser.AssignContext ctx) {
        return new AssignNode(ctx.ID().getText(), visit(ctx.exp()));
    }

    /**
     * ID '(' ( exp (',' exp)* )? ')' ';'
     **/
    @Override
    public Node visitFunCallVoid(SimpLanPlusParser.FunCallVoidContext ctx) {
        ArrayList<Node> expressions = new ArrayList<>();
        for (SimpLanPlusParser.ExpContext exp: ctx.exp()) {
            expressions.add(visit(exp));
        }
        return new CallNode(ctx.ID().getText(), expressions);
    }

    /**
     * 'if' '(' exp ')' '{' thenStm=(stm+) '}' ('else' '{' elseStm=(stm+) '}')?
     **/
    @Override
    public Node visitIfStm(SimpLanPlusParser.IfStmContext ctx) {
        ArrayList<Node> thenStm = new ArrayList<>();
        ArrayList<Node> elseStm = new ArrayList<>();
        for (SimpLanPlusParser.StmContext stm: ctx.thenStm().stm()) {
            thenStm.add(visit(stm));
        }
        if ( ctx.elseStm() != null) {
            for (SimpLanPlusParser.StmContext stm: ctx.elseStm().stm()) {
                elseStm.add(visit(stm));
            }
            return new IfStmNode(visit(ctx.exp()), thenStm, elseStm)
        }
        return new IfStmNode(visit(ctx.exp()), thenStm);
    }

    /**
     * '(' exp ')'
     **/
    @Override
    public Node visitBaseExp(SimpLanPlusParser.BaseExpContext ctx) {
        return visit(ctx.exp());
    }

    /**
     * INTEGER
     **/
    @Override
    public Node visitIntVal(SimpLanPlusParser.IntValContext ctx) {
        return new IntNode(Integer.parseInt(ctx.INTEGER().getText()));
    }

    /**
     * '!' exp
     **/
    @Override
    public Node visitNot(SimpLanPlusParser.NotContext ctx) {
        return super.visitNot(ctx);
    }

    /**
     * 'if' '(' guard=exp ')' '{' ifBranch  '}' 'else' '{' elseBranch '}'
     **/
    @Override
    public Node visitIfThenElse(SimpLanPlusParser.IfThenElseContext ctx) {
        return super.visitIfThenElse(ctx);
    }

    @Override
    public Node visitMulDivExp(SimpLanPlusParser.MulDivExpContext ctx) {
        return super.visitMulDivExp(ctx);
    }

    @Override
    public Node visitVar(SimpLanPlusParser.VarContext ctx) {
        return super.visitVar(ctx);
    }

    @Override
    public Node visitComparisonExp(SimpLanPlusParser.ComparisonExpContext ctx) {
        return super.visitComparisonExp(ctx);
    }

    @Override
    public Node visitSumSubExp(SimpLanPlusParser.SumSubExpContext ctx) {
        return super.visitSumSubExp(ctx);
    }

    @Override
    public Node visitBoolExp(SimpLanPlusParser.BoolExpContext ctx) {
        return super.visitBoolExp(ctx);
    }

    @Override
    public Node visitBoolVal(SimpLanPlusParser.BoolValContext ctx) {
        return super.visitBoolVal(ctx);
    }

    @Override
    public Node visitFunCallReturn(SimpLanPlusParser.FunCallReturnContext ctx) {
        return super.visitFunCallReturn(ctx);
    }

    @Override
    public Node visitIfBranch(SimpLanPlusParser.IfBranchContext ctx) {
        return super.visitIfBranch(ctx);
    }

    @Override
    public Node visitElseBranch(SimpLanPlusParser.ElseBranchContext ctx) {
        return super.visitElseBranch(ctx);
    }
}
