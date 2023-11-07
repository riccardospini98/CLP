//ProgLetIn
move SP FP  
pushr FP 
move SP AL 
pushr AL 
//DecNode
push 0 
//EndDecNode
//DecNode
push 0 
//EndDecNode
push function0
//AssignNode
//IntNode
storei A0 3
//endIntNode
move AL T1 
subi T1 1
load A0 0(T1) 
//EndAssignNode
//AssignNode
//IntNode
storei A0 1
//endIntNode
move AL T1 
subi T1 2
load A0 0(T1) 
//EndAssignNode
//CallNode
pushr FP 
move SP FP 
addi FP 1 
move AL T1
pushr T1 
move FP AL 
subi AL 1 
jsub function0
//EndCallNode
//IDNode
move AL T1 
subi T1 1
store A0 0(T1) 
//EndIDNode
halt
//EndProgLetInNode

function0: //FunNode
pushr RA 
//BodyNode
//IfStm
//ComparisonNode
//IDNode
move AL T1 
store T1 0(T1) //searching u at nesting 2-1
subi T1 1
store A0 0(T1) 
//EndIDNode
pushr A0  //CompExpNode 
//IntNode
storei A0 1
//endIntNode
popr T1 
bleq A0 T1 label2
label4:
storei A0 0 
b label3
label2:
beq T1 A0 label4
storei A0 1 
label3:
//EndCompExpNode 
storei T1 1 
beq A0 T1 label0
//AssignNode
//SumNode
//IDNode
move AL T1 
store T1 0(T1) //searching y at nesting 2-1
subi T1 2
store A0 0(T1) 
//EndIDNode
pushr A0 
//IntNode
storei A0 1
//endIntNode
popr T1 
add A0 T1 
popr A0 
//EndSumNode
move AL T1 
store T1 0(T1) 
subi T1 2
load A0 0(T1) 
//EndAssignNode
b label1
label0:
//AssignNode
//IDNode
move AL T1 
store T1 0(T1) //searching y at nesting 2-1
subi T1 2
store A0 0(T1) 
//EndIDNode
move AL T1 
store T1 0(T1) 
subi T1 1
load A0 0(T1) 
//EndAssignNode
label1:
//EndIfStmNode
addi SP 0
//EndBodyNode
popr RA 
addi SP 0
pop 
store FP 0(FP) 
move FP AL 
subi AL 1 
pop 
rsub RA //EndFunNode
