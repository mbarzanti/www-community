public class Civetta {
	// global vars
	public class Example {
		public static int a;
		public static int b;
	}
	public class Fish {
		
		string AbsoluteResourceInCode = "\\server\\c\\"; //AbsoluteResource
		int m_iGlobal = 0; // VIOLATION
		
		void m()
		{
			m_iGlobal = 1; 
		}
		

		public int Fish() {

			string AbsoluteResource_1 = "\\server\\c\\";  //AbsoluteResource
			string a[];
			break test; // VIOLAZ
			  
			console.writeline (a.toString); //ArrayToString
			
			if (a) { // VIOLAZ empty
			}
			
			for (int i;i<0;i++) {  // VIOLAZ empty
			}
			for (int i;i<0;i++) {  
				break;
				i++; // VIOLAZ CWE561P18
			}
			for (int i;i<0;i++) {  
				continue; // VIOLAZ unconditional continue
				i++; // VIOLAZ CWE561P18 e modifica indice for
			}
test:			
			new Thread(i);
			
			if (aAnimal instanceof Fish){  //VIOLAZ Fish è una inner class
				Fish fish = (Fish)aAnimal;
				fish.swim();
			}
			else if (aAnimal instanceof Spider){ 
				spider.crawl();
			}
			return a;
			a=1; // VIOLAZ CWE561P18
		
		}
		// long parameter list anti-pattern violated
		private int Animals(int nLegs, boolean mam, int height, int category, int speciman, string gender, boolean aquatic, int speed, int mediumWeight, int nEyes) { // VIOLAZ linea troppo lunga
			
		}
		private int m1() {
			return 0;
		}
		private int m2() {
			return 0;
		}
		private int m3() {
			return 0;
		}
		private int m4() {
			return 0;
		}
		private int m5() {
			return 0;
		}
		private int m6() {
			return 0;
		}
		private int m7() {
			return 0;
		}
		private int m8() {
			return 0;
		}
		private int m9() {  // AMC >8, violaz Top Level Functions declaring Too Many Functions is a Risk (SRA)
			return null;  // VIOLAZ ReturnNull
			return; // VIOLAZ CWE561P18
		}
	}
}
