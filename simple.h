#ifndef simple_h
#define simple_h


struct class_Obj_struct;
typedef struct class_Obj_struct* class_Obj;

struct class_Pt_struct;
typedef struct class_Pt_struct* class_Pt;

struct class_D_struct;
typedef struct class_D_struct* class_D;

struct class_String_struct;
typedef struct class_String_struct* class_String;

struct class_Boolean_struct;
typedef struct class_Boolean_struct* class_Boolean;

struct class_Nothing_struct;
typedef struct class_Nothing_struct* class_Nothing;

struct class_Int_struct;
typedef struct class_Int_struct* class_Int;

struct class_$statementsDummyClass_struct;
typedef struct class_$statementsDummyClass_struct* class_$statementsDummyClass;

struct class_P_struct;
typedef struct class_P_struct* class_P;

typedef struct obj_Obj_struct {
	class_Obj clazz;
} *obj_Obj;

typedef struct obj_Nothing_struct {
	class_Nothing clazz;
} *obj_Nothing;

typedef struct obj_String_struct {
	class_String clazz;
	char* value;
} *obj_String;

typedef struct obj_Boolean_struct {
	class_Boolean clazz;
	int value;
} *obj_Boolean;

typedef struct obj_Int_struct {
	class_Int clazz;
	int value;
} *obj_Int;

typedef struct obj_Pt_struct {
	class_Pt clazz;
	obj_Int x;
	obj_Int y;
} *obj_Pt;

typedef struct obj_P_struct {
	class_P clazz;
	obj_Int x;
	obj_Int y;
} *obj_P;

typedef struct obj_D_struct {
	class_D clazz;
} *obj_D;

typedef struct obj_$statementsDummyClass_struct {
	class_$statementsDummyClass clazz;
} *obj_$statementsDummyClass;

struct class_Obj_struct {
	obj_Obj (*constructor) ( void );
	obj_Nothing (*PRINT)  ( obj_Obj );
	obj_String (*STR)  ( obj_Obj );
	obj_Boolean (*EQUALS)  ( obj_Obj, obj_Obj );
};

struct class_Nothing_struct {
	obj_Nothing (*constructor) ( void );
	obj_Nothing (*PRINT)  ( obj_Nothing );
	obj_String (*STR)  ( obj_Nothing );
	obj_Boolean (*EQUALS)  ( obj_Nothing, obj_Obj );
};

struct class_String_struct {
	obj_String (*constructor) ( void );
	obj_Nothing (*PRINT)  ( obj_String );
	obj_String (*STR)  ( obj_String );
	obj_Boolean (*EQUALS)  ( obj_String, obj_Obj );
	obj_String (*PLUS)  ( obj_String, obj_String );
	obj_Boolean (*ATMOST)  ( obj_String, obj_String );
	obj_Boolean (*LESS)  ( obj_String, obj_String );
	obj_Boolean (*ATLEAST)  ( obj_String, obj_String );
	obj_Boolean (*MORE)  ( obj_String, obj_String );
};

struct class_Boolean_struct {
	obj_Boolean (*constructor) ( void );
	obj_Nothing (*PRINT)  ( obj_Boolean );
	obj_String (*STR)  ( obj_Boolean );
	obj_Boolean (*EQUALS)  ( obj_Boolean, obj_Obj );
};

struct class_Int_struct {
	obj_Int (*constructor) ( void );
	obj_Nothing (*PRINT)  ( obj_Int );
	obj_String (*STR)  ( obj_Int );
	obj_Boolean (*EQUALS)  ( obj_Int, obj_Obj );
	obj_Int (*PLUS)  ( obj_Int, obj_Int );
	obj_Int (*TIMES)  ( obj_Int, obj_Int );
	obj_Int (*MINUS)  ( obj_Int, obj_Int );
	obj_Int (*DIVIDE)  ( obj_Int, obj_Int );
	obj_Boolean (*ATMOST)  ( obj_Int, obj_Int );
	obj_Boolean (*LESS)  ( obj_Int, obj_Int );
	obj_Boolean (*ATLEAST)  ( obj_Int, obj_Int );
	obj_Boolean (*MORE)  ( obj_Int, obj_Int );
	obj_Int (*NEG)  ( obj_Int );
};

struct class_Pt_struct {
	obj_Pt (*constructor) ( obj_Int, obj_Int);
	obj_Nothing (*PRINT)  ( obj_Pt );
	obj_String (*STR)  ( obj_Pt );
	obj_Boolean (*EQUALS)  ( obj_Pt, obj_Obj );
	obj_Int (*foo)  ( obj_Pt, obj_Pt );
	obj_Int (*sub)  ( obj_Pt );
};

struct class_P_struct {
	obj_P (*constructor) ( obj_Int, obj_Int);
	obj_Nothing (*PRINT)  ( obj_P );
	obj_String (*STR)  ( obj_P );
	obj_Boolean (*EQUALS)  ( obj_P, obj_Obj );
	obj_Int (*foo)  ( obj_P, obj_Pt );
	obj_Int (*sub)  ( obj_P );
	obj_Int (*d)  ( obj_P, obj_Int, obj_String );
};

struct class_D_struct {
	obj_D (*constructor) ( void );
	obj_Nothing (*PRINT)  ( obj_D );
	obj_String (*STR)  ( obj_D );
	obj_Boolean (*EQUALS)  ( obj_D, obj_Obj );
};

struct class_$statementsDummyClass_struct {
	obj_$statementsDummyClass (*constructor) ( void );
	obj_Nothing (*PRINT)  ( obj_$statementsDummyClass );
	obj_String (*STR)  ( obj_$statementsDummyClass );
	obj_Boolean (*EQUALS)  ( obj_$statementsDummyClass, obj_Obj );
};

extern obj_String str_lit(char *s);
extern obj_Int int_lit(int n);
extern class_Obj class_Obj_Instance;
extern class_String class_String_Instance;
extern class_Boolean class_Boolean_Instance;
extern class_Nothing class_Nothing_Instance;
extern class_Int class_Int_Instance;
extern obj_Boolean lit_true;
extern obj_Boolean lit_false;
extern obj_Nothing nothing;


obj_Nothing Obj_method_PRINT(obj_Obj this );
obj_String Obj_method_STR(obj_Obj this );
obj_Boolean Obj_method_EQUALS(obj_Obj this, obj_Obj other );
obj_Nothing Nothing_method_PRINT(obj_Nothing this );
obj_String Nothing_method_STR(obj_Nothing this );
obj_Boolean Nothing_method_EQUALS(obj_Nothing this, obj_Obj other );
obj_Nothing String_method_PRINT(obj_String this );
obj_String String_method_STR(obj_String this );
obj_Boolean String_method_EQUALS(obj_String this, obj_Obj other );
obj_String String_method_PLUS(obj_String this, obj_String other );
obj_Boolean String_method_ATMOST(obj_String this, obj_String other );
obj_Boolean String_method_LESS(obj_String this, obj_String other );
obj_Boolean String_method_ATLEAST(obj_String this, obj_String other );
obj_Boolean String_method_MORE(obj_String this, obj_String other );
obj_Nothing Boolean_method_PRINT(obj_Boolean this );
obj_String Boolean_method_STR(obj_Boolean this );
obj_Boolean Boolean_method_EQUALS(obj_Boolean this, obj_Obj other );
obj_Nothing Int_method_PRINT(obj_Int this );
obj_String Int_method_STR(obj_Int this );
obj_Boolean Int_method_EQUALS(obj_Int this, obj_Obj other );
obj_Int Int_method_PLUS(obj_Int this, obj_Int right );
obj_Int Int_method_TIMES(obj_Int this, obj_Int right );
obj_Int Int_method_MINUS(obj_Int this, obj_Int right );
obj_Int Int_method_DIVIDE(obj_Int this, obj_Int right );
obj_Boolean Int_method_ATMOST(obj_Int this, obj_Int other );
obj_Boolean Int_method_LESS(obj_Int this, obj_Int other );
obj_Boolean Int_method_ATLEAST(obj_Int this, obj_Int other );
obj_Boolean Int_method_MORE(obj_Int this, obj_Int other );
obj_Int Int_method_NEG(obj_Int this );
obj_Nothing Pt_method_PRINT(obj_Pt this );
obj_String Pt_method_STR(obj_Pt this );
obj_Boolean Pt_method_EQUALS(obj_Pt this, obj_Obj other );
obj_Int Pt_method_foo(obj_Pt this, obj_Pt pt );
obj_Int Pt_method_sub(obj_Pt this );
obj_Nothing P_method_PRINT(obj_P this );
obj_String P_method_STR(obj_P this );
obj_Boolean P_method_EQUALS(obj_P this, obj_Obj other );
obj_Int P_method_foo(obj_P this, obj_Pt pt );
obj_Int P_method_sub(obj_P this );
obj_Int P_method_d(obj_P this, obj_Int z, obj_String q );
obj_Nothing D_method_PRINT(obj_D this );
obj_String D_method_STR(obj_D this );
obj_Boolean D_method_EQUALS(obj_D this, obj_Obj other );
obj_Nothing $statementsDummyClass_method_PRINT(obj_$statementsDummyClass this );
obj_String $statementsDummyClass_method_STR(obj_$statementsDummyClass this );
obj_Boolean $statementsDummyClass_method_EQUALS(obj_$statementsDummyClass this, obj_Obj other );


#endif
