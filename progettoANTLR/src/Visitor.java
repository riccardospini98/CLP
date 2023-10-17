import ast.*;
import ast.ExpNodes.*;
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
        System.out.println("SEP");
        return new ProgNode(visit(ctx.exp()));
    }
    /**
     * prog: (dec)+ (stm)* (exp)?
    **/
    @Override
    public Node visitRegularProg(SimpLanPlusParser.RegularProgContext ctx) {
        System.out.println("RP");
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
        System.out.println("DEC");
        return new DecNode(ctx.ID().getText(), (Type) visit( ctx.type() ));
    }

    /**
     * dec: type ID '(' (param (',' param)*)? ')' '{' body '}'
     **/
    @Override
    public Node visitFunDec(SimpLanPlusParser.FunDecContext ctx) {
        System.out.println("F-DEC");
        ArrayList<ParamNode> params = new ArrayList<>();
        for (SimpLanPlusParser.ParamContext p: ctx.param()) {
            params.add(new ParamNode(p.ID().getText(), (Type) visit(p.type())));
        }
        Node body = visit(ctx.body());
        return new FunNode(ctx.ID().getText(), (Type)visit(ctx.type()), params, body);
    }

    /**
     * parm: type ID
     **/
    /*
    @Override
    public Node visitParam(SimpLanPlusParser.ParamContext ctx) {
        System.out.println("PARAM");
        return new ParamNode(ctx.ID().getText(), (Type) visit( ctx.type() ));
    }
     */

    /**
     * body: TODO
     **/
    @Override
    public Node visitBody(SimpLanPlusParser.BodyContext ctx) {
        System.out.println("BODY");
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
        System.out.println("TYPE");
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
        System.out.println("ASGN");
        return new AssignNode(ctx.ID().getText(), visit(ctx.exp()));
    }

    /**
     * ID '(' ( exp (',' exp)* )? ')' ';'
     **/
    @Override
    public Node visitFunCallVoid(SimpLanPlusParser.FunCallVoidContext ctx) {
        System.out.println("FUNCALL_V");
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
        System.out.println("IF_STM");
        ArrayList<Node> thenStm = new ArrayList<>();
        ArrayList<Node> elseStm = new ArrayList<>();
        for (SimpLanPlusParser.StmContext stm: ctx.thenStm().stm()) {
            thenStm.add(visit(stm));
        }
        if ( ctx.elseStm() != null) {
            for (SimpLanPlusParser.StmContext stm: ctx.elseStm().stm()) {
                elseStm.add(visit(stm));
            }
            return new IfStmNode(visit(ctx.exp()), thenStm, elseStm);
        }
        return new IfStmNode(visit(ctx.exp()), thenStm);
    }

    /**
     * '(' exp ')'
     **/
    @Override
    public Node visitBaseExp(SimpLanPlusParser.BaseExpContext ctx) {
        System.out.println("BASE_EXP");
        return visit(ctx.exp());
    }

    /**
     * INTEGER
     **/
    @Override
    public Node visitIntVal(SimpLanPlusParser.IntValContext ctx) {
        System.out.println("INT");
        return new IntNode(Integer.parseInt(ctx.INTEGER().getText()));
    }

    /**
     * '!' exp
     **/
    @Override
    public Node visitNot(SimpLanPlusParser.NotContext ctx) {
        System.out.println("NOT");
        return new NotExpNode(visit(ctx.exp()));
    }

    /**
     * 'if' '(' guard=exp ')' '{' (stm)*exp  '}' 'else' '{' (stm)*exp '}'
     **/
    @Override
    public Node visitIfThenElse(SimpLanPlusParser.IfThenElseContext ctx) {
        System.out.println("ITE");
        ArrayList<Node> _stm1 = new ArrayList<Node>() ;
        ArrayList<Node> _stm2 = new ArrayList<Node>() ;

        for (SimpLanPlusParser.StmContext st : ctx.ifBranch().stm())
            _stm1.add( visit(st) );

        for (SimpLanPlusParser.StmContext st : ctx.elseBranch().stm())
            _stm2.add( visit(st) );

        return new IfExpNode(visit(ctx.guard), _stm1, visit(ctx.ifBranch().exp()), _stm2, visit(ctx.elseBranch().exp()));
    }

    /**
     * left=exp (mul='*' | div='/') right=exp
     **/
    @Override
    public Node visitMulDivExp(SimpLanPlusParser.MulDivExpContext ctx) {
        System.out.println("MULDIV");
        if (ctx.op.getText().equals("/")) {
            return new DivNode(visit(ctx.left), visit(ctx.right));
        } else {
            return new MultNode(visit(ctx.left), visit(ctx.right));
        }
    }
    /**
     * ID
     **/
    @Override
    public Node visitVar(SimpLanPlusParser.VarContext ctx) {
        System.out.println("VAR");
        return new IdNode(ctx.ID().getText());
    }

    /**
     * left=exp (gt='>' | lt='<' | gte='>=' | lte='<=' | eq='==') right=exp
     **/
    @Override
    public Node visitComparisonExp(SimpLanPlusParser.ComparisonExpContext ctx) {
        System.out.println("COMP");
        return new CompNode(ctx.op.getText(), visit(ctx.left), visit(ctx.right));
    }

    /**
     * left=exp (sum='+' | sub='-') right=exp
     **/
    @Override
    public Node visitSumSubExp(SimpLanPlusParser.SumSubExpContext ctx) {
        System.out.println("SUMSUB");
        if(ctx.op.getText().equals("+")) {
            return new SumNode(visit(ctx.left), visit(ctx.right));
        } else {
            return new SubNode(visit(ctx.left), visit(ctx.right));
        }
    }

    /**
     * left=exp (and='&&' | or='||') right=exp
     **/
    @Override
    public Node visitBoolExp(SimpLanPlusParser.BoolExpContext ctx) {
        System.out.println("BOOL_E");
        if (ctx.op.getText().equals("&&")) {
            return new AndNode(visit(ctx.left), visit(ctx.right));
        } else {

            return new OrNode(visit(ctx.left), visit(ctx.right));
        }
    }

    /**
     * exp: ('true' | 'false')
     */
    @Override
    public Node visitBoolVal(SimpLanPlusParser.BoolValContext ctx) {
        System.out.println("BOOL");
        return new BoolNode(Boolean.parseBoolean(ctx.getText()));
    }

    /**
     * exp: ID '(' (exp (',' exp)* )? ')'
     */
    @Override
    public Node visitFunCallReturn(SimpLanPlusParser.FunCallReturnContext ctx) {
        System.out.println("FUNCALL_R");
        ArrayList<Node> args = new ArrayList<Node>();
        for (SimpLanPlusParser.ExpContext exp : ctx.exp())
            args.add(visit(exp));
        return new CallNode(ctx.ID().getText(), args);

    }
}
