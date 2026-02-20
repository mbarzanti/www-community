/*
More stale closure bugs
But stale closures won’t just appear in useEffect. They can also turn up in event handlers and other closures inside your React components. Let’s have a look at a React component with a stale event handler; we’ll create a scroll progress bar that does the following:

increases its width along the screen as the user scrolls
starts transparent and becomes more and more opaque as the user scrolls
provides the user with a button that randomizes the color of the scroll bar
We’re going to leave the progress bar outside of the React tree and update it in the event handler. Here’s our buggy implementation:
*/
import React from 'https://cdn.skypack.dev/react';
import ReactDOM from 'https://cdn.skypack.dev/react-dom';

const {useState,useEffect} = React; 

function Scroller(){
  const [scrollPosition, setScrollPosition] = useState(window.scrollY);
  const [color,setColor] = useState({r:200,g:100,b:100});
  
 
  useEffect(()=>{
   document.addEventListener("scroll",handleScroll);
    return ()=>{document.removeEventListener("scroll",handleScroll);}
  },[]);
  
  function onColorChange(){
    setColor({r:100+Math.random()*155,g:100+Math.random()*155,b:100+Math.random()*155});
  }
  
  function handleScroll(e){
    const scrollDistance = document.body.scrollTop || document.documentElement.scrollTop;
    const documentHeight = document.documentElement.scrollHeight - document.documentElement.clientHeight;
    const percentAlong =  (scrollDistance / documentHeight);
    const progress = document.getElementById("progress");
    progress.style.width = `${percentAlong*100}%`;
    progress.style.backgroundColor = `rgba(${color.r},${color.g},${color.b},${percentAlong})`;
    setScrollPosition(percentAlong);
  }
  
  return <div className="scroller" style={{backgroundColor:`rgb(${color.r},${color.g},${color.b})`}}>
    <button onClick={onColorChange}>Change color</button>
    <span class="percent">{Math.round(scrollPosition* 100)}%</span>
  </div>
}

ReactDOM.render(<Scroller/>,document.getElementById("root"))
/*
Our bar gets wider and increasingly more opaque as the page scrolls. But if you click the change color button, our randomized colors are not affecting the progress bar. We’re getting this bug because the closure is affected by component state, and this closure is never being re-declared so we only get the original value of the state and no updates.

You can see how setting up closures that call external APIs using React state, or component props might give you grief if you’re not careful.

The solution
Again, there are multiple ways to fix this problem. We could keep the color state in a mutable ref which we could later use in our event handler:

const [color,setColor] = useState({r:200,g:100,b:100});
const colorRef = useRef(color);

function onColorChange(){
  const newColor = {r:100+Math.random()*155,g:100+Math.random()*155,b:100+Math.random()*155};
  setColor(newColor);
  colorRef.current=newColor;
  progress.style.backgroundColor = `rgba(${newColor.r},${newColor.g},${newColor.b},${scrollPosition})`;
}
*/