move SP FP  
pushr FP 
move SP AL 
pushr AL 
pushr A0 
pushr A0 
pushr A0 
move AL T1 
subi T1 1
store A0 0(T1) 
halt
