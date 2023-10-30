//ProgLetIn
move SP FP  
pushr FP 
move SP AL 
pushr AL 
//DecNode
push 0 
//EndDecNode
push function0
//AssignNode
//CallNode
pushr FP 
move SP FP 
addi FP 1 
move AL T1
pushr T1 
//IntNode
storei A0 5
//endIntNode
pushr A0
move FP AL 
subi AL 1 
jsub function0
//EndCallNode
move AL T1 
store T1 0(T1) 
subi T1 1
load A0 0(T1) 
//EndAssignNode
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
//SumNode
//IDNode
move AL T1 
store T1 0(T1) //searching x at nesting 3-2
subi T1 1
store A0 0(T1) 
//EndIDNode
pushr A0 
//IDNode
move AL T1 
store T1 0(T1) //searching x at nesting 3-2
subi T1 1
store A0 0(T1) 
//EndIDNode
popr T1 
add A0 T1 
popr A0 
//EndSumNode
addi SP 0
//EndBodyNode
popr RA 
addi SP 1
pop 
store FP 0(FP) 
move FP AL 
subi AL 1 
pop 
rsub RA //EndFunNode
