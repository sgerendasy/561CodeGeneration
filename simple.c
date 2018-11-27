#include <stdio.h>
#include <stdlib.h>
#include <String.h>
#include <stdlib.h>
#include "simple.h"


class_Obj the_class_Obj; 
obj_Obj new_Obj(  ) { 
obj_Obj new_thing = (obj_Obj) malloc(sizeof(struct obj_Obj_struct));
new_thing->clazz = the_class_Obj;
return new_thing; 
}
obj_String Obj_method_STRING(obj_Obj this) {
long addr = (long) this;
char *rep;
asprintf(&rep, "<Object at %ld>", addr);
obj_String str = str_literal(rep); 
return str; 
}
obj_Obj Obj_method_PRINT(obj_Obj this) {
  obj_String str = this->clazz->STRING(this);
  fprintf(stdout, "%s", str->text);
  return this; 
}
obj_Boolean Obj_method_EQUALS(obj_Obj this, obj_Obj other) {
  if (this == other) {
    return lit_true;
  } else {
    return lit_false; 
} 
}
 struct  class_Obj_struct  the_class_Obj_struct = {
  new_Obj,     
  Obj_method_STRING, 
  Obj_method_PRINT, 
  Obj_method_EQUALS 
};
class_Obj the_class_Obj = &the_class_Obj_struct;
obj_String new_String(  ) {
  obj_String new_thing = (obj_String) malloc(sizeof(struct obj_String_struct));
  new_thing->clazz = the_class_String;
  return new_thing; 
}
obj_String String_method_STRING(obj_String this) {
  return this;
}
obj_String String_method_PRINT(obj_String this) {
  fprintf(stdout, "%s", this->text);
  return this;
}
obj_Boolean String_method_EQUALS(obj_String this, obj_Obj other) {
  obj_String other_str = (obj_String) other;
  if (other_str->clazz != the_class_String) {
    return lit_false;
  }
  if (strcmp(this->text,other_str->text) == 0) {
    return lit_true;
  } else {
    return lit_false;
  }
}
struct  class_String_struct  the_class_String_struct = {
  new_String,     
  String_method_STRING, 
  String_method_PRINT, 
  String_method_EQUALS
};
class_String the_class_String = &the_class_String_struct; 
obj_String str_literal(char *s) {
  char *rep;
  obj_String str = the_class_String->constructor(); 
  str->text = s;
  return str;
}
obj_Nothing new_Nothing(  ) {
  return nothing; 
}
obj_String Nothing_method_STRING(obj_Nothing this) {
    return str_literal("<nothing>");
}
obj_Boolean new_Boolean(  ) {
  obj_Boolean new_thing = (obj_Boolean)
    malloc(sizeof(struct obj_Boolean_struct));
  new_thing->clazz = the_class_Boolean;
  return new_thing; 
}
obj_String Boolean_method_STRING(obj_Boolean this) {
  if (this == lit_true) {
    return str_literal("true");
  } else if (this == lit_false) {
    return str_literal("false");
  } else {
    return str_literal("!!!BOGUS BOOLEAN");
  }
}
struct  class_Boolean_struct  the_class_Boolean_struct = {
  new_Boolean,     
  Boolean_method_STRING, 
  Obj_method_PRINT, 
  Obj_method_EQUALS
};
class_Boolean the_class_Boolean = &the_class_Boolean_struct; 
struct obj_Boolean_struct lit_false_struct =
  { &the_class_Boolean_struct, 0 };
obj_Boolean lit_false = &lit_false_struct;
struct obj_Boolean_struct lit_true_struct =
  { &the_class_Boolean_struct, 1 };
obj_Boolean lit_true = &lit_true_struct;
obj_Int new_Int(  ) {
  obj_Int new_thing = (obj_Int)
    malloc(sizeof(struct obj_Int_struct));
  new_thing->clazz = the_class_Int;
  new_thing->value = 0;          
  return new_thing; 
}
obj_String Int_method_STRING(obj_Int this) {
  char *rep;
  asprintf(&rep, "%d", this->value);
  return str_literal(rep); 
}
obj_Boolean Int_method_EQUALS(obj_Int this, obj_Obj other) {
  obj_Int other_int = (obj_Int) other; 
  
  if (other_int->clazz != this->clazz) {
    return lit_false;
  }
  if (this->value != other_int->value) {
    return lit_false;
  }
  return lit_true;
}
obj_Int Int_method_PLUS(obj_Int this, obj_Int other) {
  return int_literal(this->value + other->value);
}
obj_Boolean Int_method_LESS(obj_Int this, obj_Int other) {
  if (this->value < other->value) {
    return lit_true;
  }
  return lit_false;
}
struct  class_Int_struct  the_class_Int_struct = {
  new_Int,     
  Int_method_STRING, 
  Obj_method_PRINT, 
  Int_method_EQUALS,
  Int_method_LESS,
  Int_method_PLUS
};
class_Int the_class_Int = &the_class_Int_struct; 
obj_Int int_literal(int n) {
  obj_Int boxed = new_Int();
  boxed->value = n;
  return boxed;
}
