#include <stdio.h>
#include <stdlib.h>
#include <String.h>
#include <stdlib.h>
#include "custom.h"


class_Obj class_Obj_Instance; 
obj_Obj new_Obj(  ) { 
obj_Obj new_thing = (obj_Obj) malloc(sizeof(struct obj_Obj_struct));
new_thing->clazz = class_Obj_Instance;
return new_thing; 
}
obj_Nothing Obj_method_PRINT(obj_Obj this) {
  obj_String str = this->clazz->STR(this);
  fprintf(stdout, "%s", str->value);
  return nothing; 
}
obj_String Obj_method_STRING(obj_Obj this) {
long addr = (long) this;
char *rep;
asprintf(&rep, "<Object at %ld>", addr);
obj_String str = str_lit(rep); 
return str; 
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
  Obj_method_PRINT, 
  Obj_method_STR, 
  Obj_method_EQUALS 
};
class_Obj class_Obj_Instance = &the_class_Obj_struct;
obj_String new_String(  ) {
  obj_String new_thing = (obj_String) malloc(sizeof(struct obj_String_struct));
  new_thing->clazz = class_String_Instance;
  return new_thing; 
}
obj_String String_method_PLUS(obj_String this, obj_String other) {
char* thisString = this->value;
char* otherString = other->value;
strcat(thisString, otherString);
return str_lit(thisString);
}

obj_Boolean String_method_EQUALS(obj_String this, obj_Obj other) {
  obj_String other_str = (obj_String) other;
  if (other_str->clazz != class_String_Instance) {
    return lit_false;
  }
  if (strcmp(this->value,other_str->value) == 0) {
    return lit_true;
  } else {
    return lit_false;
  }
}
obj_Boolean String_method_ATMOST(obj_String this, obj_String other) {
  if (strcmp(this->value, other->value) <= 0) {
    return lit_true;
  } 
  else {
    return lit_false;
}
}
obj_Boolean String_method_LESS(obj_String this, obj_String other) {
  if (strcmp(this->value, other->value) < 0) {
    return lit_true;
  } else {
    return lit_false;
}
}
obj_Boolean String_method_ATLEAST(obj_String this, obj_String other) {
   if (strcmp(this->value, other->value) >= 0) {
      return lit_true;
    } else {
      return lit_false;
}
}
obj_Boolean String_method_MORE(obj_String this, obj_String other) {
  if (strcmp(this->value, other->value) > 0) {
    return lit_true;
  } else {
    return lit_false;
}
}
struct  class_String_struct  the_class_String_struct = {
  new_String,   
  String_method_PRINT, 
  String_method_STR, 
  String_method_EQUALS,
  String_method_PLUS,
  String_method_ATMOST,
  String_method_LESS,
  String_method_ATLEAST,
  String_method_MORE
 };
class_String class_String_Instance = &the_class_String_struct; 
obj_String str_lit(char *s) {
  char *rep;
  obj_String str = class_String_Instance->constructor(); 
  str->value = s;
  return str;
}
obj_Int new_Int(  ) {
  obj_Int new_thing = (obj_Int)
    malloc(sizeof(struct obj_Int_struct));
  new_thing->clazz = class_Int_Instance;
  new_thing->value = 0;          
  return new_thing; 
}
obj_Int Int_method_PLUS(obj_Int this, obj_Int other) {
  return int_lit(this->value + other->value);
}
obj_Int Int_method_TIMES(obj_Int this, obj_Int other) {
  return int_lit(this->value * other->value);
}
obj_Int Int_method_MINUS(obj_Int this, obj_Int other) {
  return int_lit(this->value - other->value);
}
obj_Boolean Int_method_ATMOST(obj_Int this, obj_Int other) {
  if (this->value <= other->value) {
    return lit_true;
}
  return lit_false;
}
obj_Boolean Int_method_LESS(obj_Int this, obj_Int other) {
  if (this->value < other->value) {
    return lit_true;
}
  return lit_false;
}
obj_Boolean Int_method_ATLEAST(obj_Int this, obj_Int other) {
  if (this->value >= other->value) {
    return lit_true;
}
  return lit_false;
}
obj_Boolean Int_method_MORE(obj_Int this, obj_Int other) {
  if (this->value > other->value) {
    return lit_true;
}
  return lit_false;
}
struct  class_Int_struct  the_class_Int_struct = {
  new_Int,     /* Constructor */
  Int_method_PRINT, 
  Int_method_STR, 
  Int_method_EQUALS,
  Int_method_PLUS,
  Int_method_MINUS,
  Int_method_TIMES,
  Int_method_DIVIDE,
  Int_method_ATMOST,
  Int_method_LESS,
  Int_method_ATLEAST,
  Int_method_MORE
};

class_Int class_Int_Instance = &the_class_Int_struct; 
obj_Int int_lit(int n) {
  obj_Int boxed = new_Int();
  boxed->value = n;
  return boxed;
}
