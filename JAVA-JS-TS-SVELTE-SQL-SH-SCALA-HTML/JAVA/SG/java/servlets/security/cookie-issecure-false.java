class BadCISF {
          public void bad1() {
              // ruleid: cookie-issecure-false
              Cookie cookie = new Cookie("name", "value");
          }
   }

class OkCISF {
          public void ok1() {
             // ok: cookie-issecure-false
             Cookie cookie = new Cookie("name", "value");
             cookie.setSecure(true);
          }
}
