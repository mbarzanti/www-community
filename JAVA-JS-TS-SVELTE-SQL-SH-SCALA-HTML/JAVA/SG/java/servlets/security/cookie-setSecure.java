class BadCSSF {

          public void bad2() {
              Cookie cookie = new Cookie("name", "value");
              // ruleid: cookie-setSecure
              cookie.setSecure(false);
          }
   }

class OkCSSF {
          public void ok1() {
             // ok: cookie-setSecure
             Cookie cookie = new Cookie("name", "value");
             cookie.setSecure(true);
          }
}
