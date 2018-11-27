#ifndef custom_h
#define custom_h


struct class_Obj_struct;
typedef struct class_Obj_struct* class_Obj;

struct class_String_struct;
typedef struct class_String_struct* class_String;

struct class_Boolean_struct;
typedef struct class_Boolean_struct* class_Boolean;

struct class_Nothing_struct;
typedef struct class_Nothing_struct* class_Nothing;

struct class_Int_struct;
typedef struct class_Int_struct* class_Int;

struct class_C1_struct;
typedef struct class_C1_struct* class_C1;

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

typedef struct obj_C1_struct {
	class_C1 clazz;
	obj_Int x;
} *obj_C1;

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
};

struct class_C1_struct {
	obj_C1 (*constructor) ( obj_Int, obj_Obj);
	obj_Nothing (*PRINT)  ( obj_C1 );
	obj_String (*STR)  ( obj_C1 );
	obj_Boolean (*EQUALS)  ( obj_C1, obj_Obj );
	obj_String (*foo)  ( obj_C1, obj_Boolean, obj_Int );
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
obj_Nothing C1_method_PRINT(obj_C1 this );
obj_String C1_method_STR(obj_C1 this );
obj_Boolean C1_method_EQUALS(obj_C1 this, obj_Obj other );
obj_String C1_method_foo(obj_C1 this, obj_Boolean a, obj_Int c );


#endif
