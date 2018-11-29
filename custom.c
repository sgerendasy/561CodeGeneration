#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "custom.h"


void quackmain(); 

int main(int argc, char** argv) {
  quackmain();
  printf("--- Terminated successfully (woot!) ---");
  exit(0);
}


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

obj_String Obj_method_STR(obj_Obj this) {
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

obj_Nothing new_Nothing(  ) {
  return nothing; 
}

obj_Nothing Nothing_method_PRINT(obj_Nothing this) {
  obj_String str = this->clazz->STR(this);
  fprintf(stdout, "%s", str->value);
  return nothing; 
}

obj_String Nothing_method_STR(obj_Nothing this) {
    return str_lit("<nothing>");
}

obj_Boolean Nothing_method_EQUALS(obj_Nothing this, obj_Obj other) {
obj_Nothing other_nothing = (obj_Nothing) other;
  if (this == other_nothing) {
    return lit_true;
  } else {
    return lit_false; 
} 
}

struct  class_Nothing_struct  the_class_Nothing_struct = {
  new_Nothing,     
  Nothing_method_PRINT, 
  Nothing_method_STR, 
  Nothing_method_EQUALS
};

class_Nothing class_Nothing_Instance = &the_class_Nothing_struct; 

struct obj_Nothing_struct nothing_struct =
  { &the_class_Nothing_struct };

obj_Nothing nothing = &nothing_struct; 

obj_String new_String(  ) {
  obj_String new_thing = (obj_String) malloc(sizeof(struct obj_String_struct));
  new_thing->clazz = class_String_Instance;
  return new_thing; 
}

obj_String String_method_PLUS(obj_String this, obj_String other) {
char* thisString = this->value;
char* otherString = other->value;
char* combinedStrings = malloc(strlen(thisString) + strlen(otherString) + 1);
strcat(combinedStrings, thisString);
strcat(combinedStrings, otherString);
return str_lit(combinedStrings);
}

obj_Boolean String_method_EQUALS(obj_String this, obj_Obj other) {
  obj_String other_str = (obj_String) other;
  if (other_str->clazz != class_String_Instance) {
    return lit_false;
  }
  if (strcmp(this->value, other_str->value) == 0) {
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

obj_Nothing String_method_PRINT(obj_String this) {
  fprintf(stdout, "%s", this->value);
  return nothing;
}

obj_String String_method_STR(obj_String this) {
  return this;
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

obj_Boolean new_Boolean(  ) {
  obj_Boolean new_thing = (obj_Boolean)
    malloc(sizeof(struct obj_Boolean_struct));
  new_thing->clazz = class_Boolean_Instance;
  return new_thing; 
}

obj_Nothing Boolean_method_PRINT(obj_Boolean this) {
  obj_String str = this->clazz->STR(this);
  fprintf(stdout, "%s", str->value);
  return nothing; 
}

obj_String Boolean_method_STR(obj_Boolean this) {
  if (this == lit_true) {
    return str_lit("true");
  } else if (this == lit_false) {
    return str_lit("false");
  } else {
    return str_lit("!!!BOGUS BOOLEAN");

  }
}
obj_Boolean Boolean_method_EQUALS(obj_Boolean this, obj_Obj other) {
obj_Boolean other_bool = (obj_Boolean) other;
  if (this->value == other_bool->value) {
    return lit_true;
  } else {
    return lit_false; 
} 
}

struct  class_Boolean_struct  the_class_Boolean_struct = {
  new_Boolean,     
  Boolean_method_PRINT, 
  Boolean_method_STR, 
  Boolean_method_EQUALS
};

class_Boolean class_Boolean_Instance = &the_class_Boolean_struct; 

struct obj_Boolean_struct lit_false_struct =
  { &the_class_Boolean_struct, 0 };

obj_Boolean lit_false = &lit_false_struct;

struct obj_Boolean_struct lit_true_struct =
  { &the_class_Boolean_struct, 1 };

obj_Boolean lit_true = &lit_true_struct;

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

obj_Int Int_method_DIVIDE(obj_Int this, obj_Int other) {
  return int_lit(this->value / other->value);
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

obj_Int Int_method_NEG(obj_Int this) {
  return int_lit(- this->value);
}

obj_Nothing Int_method_PRINT(obj_Int this) {
  obj_String str = this->clazz->STR(this);
  fprintf(stdout, "%s", str->value);
  return nothing; 
}

obj_String Int_method_STR(obj_Int this) {
  char *rep;
  asprintf(&rep, "%d", this->value);
  return str_lit(rep); 
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

struct  class_Int_struct  the_class_Int_struct = {
  new_Int,     /* Constructor */
  Int_method_PRINT, 
  Int_method_STR, 
  Int_method_EQUALS,
  Int_method_PLUS,
  Int_method_TIMES,
  Int_method_MINUS,
  Int_method_DIVIDE,
  Int_method_ATMOST,
  Int_method_LESS,
  Int_method_ATLEAST,
  Int_method_MORE,
  Int_method_NEG
};

class_Int class_Int_Instance = &the_class_Int_struct; 

obj_Int int_lit(int n) {
  obj_Int boxed = new_Int();
  boxed->value = n;
  return boxed;
}

void quackmain() {

	obj_Int temp_0 = int_lit(1);
	obj_Int temp_2 = int_lit(2);
	obj_Int temp_4 = temp_0;
	obj_Int temp_5 = int_lit(5);
	obj_Int temp_3 = Int_method_TIMES( temp_4, temp_5);
	obj_Int temp_1 = Int_method_PLUS( temp_2, temp_3);
	obj_String temp_6 = str_lit("left");
	obj_String temp_8 = temp_6;
	obj_String temp_9 = str_lit("right");
	obj_String temp_7 = String_method_PLUS( temp_8, temp_9);
	obj_Int temp_11 = int_lit(6);
	obj_Int temp_10 = Int_method_NEG( temp_11);
	obj_Int temp_13 = temp_1;
	obj_Int temp_12 = Int_method_NEG( temp_13);
	obj_String temp_14 = Int_method_STR(temp_0);
	obj_Int temp_17 = temp_1;
	obj_Int temp_18 = temp_12;
	obj_Int temp_16 = Int_method_PLUS( temp_17, temp_18);
	obj_Int temp_19 = int_lit(4);
	return Int_method_MINUS( temp_16, temp_19);
	return 0;
}
