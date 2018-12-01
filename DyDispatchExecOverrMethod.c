#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "DyDispatchExecOverrMethod.h"


void quackmain(); 

int main(int argc, char** argv) {
  printf("--- Begin: %s ---\n", argv[0]);

  quackmain();
  printf("\n--- Terminated successfully ---\n");

  exit(0);
}


class_Obj class_Obj_Instance; 
obj_Obj new_Obj(  ) { 
obj_Obj new_thing = (obj_Obj) malloc(sizeof(struct obj_Obj_struct));
new_thing->clazz = class_Obj_Instance;
return new_thing; 
}

obj_Nothing Obj_method_PRINT(obj_Obj self) {
  obj_String str = self->clazz->STR(self);
  fprintf(stdout, "%s", str->value);
  return nothing; 
}

obj_String Obj_method_STR(obj_Obj self) {
long addr = (long) self;
char *rep;
asprintf(&rep, "<Object at %ld>", addr);
obj_String str = str_lit(rep); 
return str; 
}

obj_Boolean Obj_method_EQUALS(obj_Obj self, obj_Obj other) {
  if (self == other) {
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

obj_String String_method_PLUS(obj_String self, obj_String other) {
char* selfString = self->value;
char* otherString = other->value;
char* combinedStrings = malloc(strlen(selfString) + strlen(otherString) + 1);
strcat(combinedStrings, selfString);
strcat(combinedStrings, otherString);
return str_lit(combinedStrings);
}

obj_Boolean String_method_EQUALS(obj_String self, obj_Obj other) {
  obj_String other_str = (obj_String) other;
  if (other_str->clazz != class_String_Instance) {
    return lit_false;
  }
  if (strcmp(self->value, other_str->value) == 0) {
    return lit_true;
  } else {
    return lit_false;
  }
}

obj_Boolean String_method_ATMOST(obj_String self, obj_String other) {
  if (strcmp(self->value, other->value) <= 0) {
    return lit_true;
  } 
  else {
    return lit_false;
}
}

obj_Boolean String_method_LESS(obj_String self, obj_String other) {
  if (strcmp(self->value, other->value) < 0) {
    return lit_true;
  } else {
    return lit_false;
}
}

obj_Boolean String_method_ATLEAST(obj_String self, obj_String other) {
   if (strcmp(self->value, other->value) >= 0) {
      return lit_true;
    } else {
      return lit_false;
}
}

obj_Boolean String_method_MORE(obj_String self, obj_String other) {
  if (strcmp(self->value, other->value) > 0) {
    return lit_true;
  } else {
    return lit_false;
}
}

obj_Nothing String_method_PRINT(obj_String self) {
  fprintf(stdout, "%s", self->value);
  return nothing;
}

obj_String String_method_STR(obj_String self) {
  return self;
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

obj_Nothing Boolean_method_PRINT(obj_Boolean self) {
  obj_String str = self->clazz->STR(self);
  fprintf(stdout, "%s", str->value);
  return nothing; 
}

obj_String Boolean_method_STR(obj_Boolean self) {
  if (self == lit_true) {
    return str_lit("true");
  } else if (self == lit_false) {
    return str_lit("false");
  } else {
    return str_lit("!!!BOGUS BOOLEAN");

  }
}
obj_Boolean Boolean_method_EQUALS(obj_Boolean self, obj_Obj other) {
obj_Boolean other_bool = (obj_Boolean) other;
  if (self->value == other_bool->value) {
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

obj_Nothing new_Nothing(  ) {
  return nothing; 
}

obj_Nothing Nothing_method_PRINT(obj_Nothing self) {
  obj_String str = self->clazz->STR(self);
  fprintf(stdout, "%s", str->value);
  return nothing; 
}

obj_String Nothing_method_STR(obj_Nothing self) {
    return str_lit("<nothing>");
}

obj_Boolean Nothing_method_EQUALS(obj_Nothing self, obj_Obj other) {
obj_Nothing other_nothing = (obj_Nothing) other;
  if (self == other_nothing) {
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

obj_Int new_Int(  ) {
  obj_Int new_thing = (obj_Int)
    malloc(sizeof(struct obj_Int_struct));
  new_thing->clazz = class_Int_Instance;
  new_thing->value = 0;
  return new_thing; 
}
obj_Int Int_method_PLUS(obj_Int self, obj_Int other) {
  return int_lit(self->value + other->value);
}

obj_Int Int_method_TIMES(obj_Int self, obj_Int other) {
  return int_lit(self->value * other->value);
}

obj_Int Int_method_MINUS(obj_Int self, obj_Int other) {
  return int_lit(self->value - other->value);
}

obj_Int Int_method_DIVIDE(obj_Int self, obj_Int other) {
  return int_lit(self->value / other->value);
}

obj_Boolean Int_method_ATMOST(obj_Int self, obj_Int other) {
  if (self->value <= other->value) {
    return lit_true;
}
  return lit_false;
}

obj_Boolean Int_method_LESS(obj_Int self, obj_Int other) {
  if (self->value < other->value) {
    return lit_true;
}
  return lit_false;
}

obj_Boolean Int_method_ATLEAST(obj_Int self, obj_Int other) {
  if (self->value >= other->value) {
    return lit_true;
}
  return lit_false;
}

obj_Boolean Int_method_MORE(obj_Int self, obj_Int other) {
  if (self->value > other->value) {
    return lit_true;
}
  return lit_false;
}

obj_Int Int_method_NEG(obj_Int self) {
  return int_lit(- self->value);
}

obj_Nothing Int_method_PRINT(obj_Int self) {
  obj_String str = self->clazz->STR(self);
  fprintf(stdout, "%s", str->value);
  return nothing; 
}

obj_String Int_method_STR(obj_Int self) {
  char *rep;
  asprintf(&rep, "%d", self->value);
  return str_lit(rep); 
}

obj_Boolean Int_method_EQUALS(obj_Int self, obj_Obj other) {
  obj_Int other_int = (obj_Int) other; 
  
  if (other_int->clazz != self->clazz) {
    return lit_false;
  }
  if (self->value != other_int->value) {
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

obj_Pt new_Pt(obj_Int x ,obj_Int y ) {
  obj_Pt new_thing = (obj_Pt) malloc(sizeof(struct obj_Pt_struct));
  new_thing->clazz = class_Pt_Instance;
  new_thing->x = x;
  new_thing->y = y;
  return new_thing; 
}

obj_Pt Pt_method_PLUS(obj_Pt self, obj_Pt other) {
	return new_Pt(Int_method_PLUS( self->x , other->x), Int_method_PLUS( self->y , other->y));

}
struct  class_Pt_struct  the_class_Pt_struct = {
new_Pt,
Obj_method_PRINT,
Obj_method_STR,
Obj_method_EQUALS,
Pt_method_PLUS,
};
class_Pt class_Pt_Instance = &the_class_Pt_struct; 
void quackmain() {
	obj_Pt temp_0 = new_Pt(int_lit(4), int_lit(5));
	obj_Pt temp_1 = new_Pt(int_lit(1), int_lit(2));
	obj_Pt temp_2 = Pt_method_PLUS(temp_0, temp_1);
	obj_Pt temp_3 = Pt_method_PLUS( temp_0, temp_1);
}
