/*
Buggy code #3: Stale closure bugs
This is a common issue with React hooks. There’s previously been a CSS-Tricks article about dealing with stale props and states in React’s functional components.

Let’s take a look at a few situations where you might run into trouble. The first crops up is when using useEffect. If we’re doing anything asynchronous inside of useEffect we can get into trouble using old state or props.

Here’s an example. We need to increment a count every second. We set it up on the first render with a useEffect, providing a closure that increments the count as the first argument, and an empty array as the second argument. We’ll give it the empty array as we don’t want React to restart the interval on every render.
*/
import React from 'https://cdn.skypack.dev/react';
import ReactDOM from 'https://cdn.skypack.dev/react-dom';

const {useEffect,useState} = React; 

function Counter() {
  let [count, setCount] = useState(0);

  useEffect(() => {
    let id = setInterval(() => {
      setCount(count + 1);
    }, 1000);
    return () => clearInterval(id);
  },[]);

  return <h1>{count}</h1>;
}

ReactDOM.render(<Counter/>,document.getElementById("root"))
/*
Oh no! The count gets incremented to 1 but never changes after that! Why is this happening?

It’s to do with two things:

the behavior of closures in JavaScript
the second argument of that useEffect call
Having a look at the MDN docs on closures, we can see:

A closure is the combination of a function and the lexical environment within which that function was declared. This environment consists of any local variables that were in-scope at the time the closure was created.

The “lexical environment” in which our useEffect closure is declared is inside our Counter React component. The local variable we’re interested is count, which is zero at the time of the declaration (the first render).

The problem is, this closure is never declared again. If the count is zero at the time declaration, it will always be zero. Each time the interval fires, it’s running a function that starts with a count of zero and increments it to 1.

So how might we get the function declared again? This is where the second argument of the useEffect call comes in. We thought we were extremely clever only starting off the interval once by using the empty array, but in doing so we shot ourselves in the foot. If we had left out this argument, the closure inside useEffect would get declared again with a new count every time.

The way I like to think about it is that the useEffect dependency array does two things:

It will fire the useEffect function when the dependency changes.
It will also redeclare the closure with the updated dependency, keeping the closure safe from stale state or props.
In fact, there’s even a lint rule to keep your useEffect instances safe from stale state and props by making sure you add the right dependencies to the second argument.

But we don’t actually want to reset our interval every time the component gets rendered either. How do we solve this problem then?

The solution
Again, there are multiple solutions to our problem here. Let’s start with the easiest: not using the count state at all and instead passing a function into our setState call:

function Counter() { 
  let [count, setCount] = useState(0);

  useEffect(() => {
    let id = setInterval(() => {
      setCount(prevCount => prevCount+ 1);
    }, 1000);
    return () => clearInterval(id);
  },[]);

  return <h1>{count}</h1>;
}
That was easy. Another option is to use the useRef hook like this to keep a mutable reference of the count:

function Counter() {
  let [count, setCount] = useState(0);
  const countRef = useRef(count)
  
  function updateCount(newCount){
    setCount(newCount);
    countRef.current = newCount;
  }

  useEffect(() => {
    let id = setInterval(() => {
      updateCount(countRef.current + 1);
    }, 1000);
    return () => clearInterval(id);
  },[]);

  return <h1>{count}</h1>;
}

ReactDOM.render(<Counter/>,document.getElementById("root"))
*/