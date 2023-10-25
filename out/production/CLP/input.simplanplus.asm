//ProgLetIn
move SP FP  
pushr FP 
move SP AL 
pushr AL 
//DecNode
subi SP 1 
//DecNode
subi SP 1 
//DecNode
subi SP 1 
//AssignNode
//IntNode
storei A0 2
move AL T1 
store T1 0(T1) 
subi T1 3
load A0 0(T1) 
//IfStm
//ComparisonNode
//IDNode
move AL T1 
subi T1 3
store A0 0(T1) 
pushr A0  //CompExpNode 
//IntNode
storei A0 1
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
//IDNode
move AL T1 
subi T1 2
store A0 0(T1) 
move AL T1 
store T1 0(T1) 
subi T1 1
load A0 0(T1) 
b label1
label0:
//AssignNode
//IDNode
move AL T1 
subi T1 3
store A0 0(T1) 
move AL T1 
store T1 0(T1) 
subi T1 2
load A0 0(T1) 
label1:
halt
