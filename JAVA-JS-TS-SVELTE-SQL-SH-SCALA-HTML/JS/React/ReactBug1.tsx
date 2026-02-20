/*
Buggy code #1: Mutating state and props
It’s a big anti-pattern to mutate state or props in React. Don’t do this!

This is not a revolutionary piece of advice—it’s usually one of the first things you learn if you’re getting started with React. 
But you might think you can get away with it (because it seems like you can in some cases).

I’m going to show you how bugs might creep into your code if you’re mutating props. 
Sometimes you’ll want a component that will show a transformed version of some data. Let’s create a parent component 
that holds a count in state and a button that will increment it. We’ll also make a child component that receives the count 
via props and shows what the count would look like with 5 added to it
More experienced JavaScript programmers will know that the big difference here is that primitive types such as numbers, 
booleans and strings are immutable and passed by value, whereas objects are passed by reference.

This means that

If you put a number in a variable, assign another variable to it, then change the second variable, the first variable will not be changed.
If you if you put an object in a variable, assign another variable to it, then change the second variable, 
the first variable will get changed.
When the child component changes a property of the state object, it’s adding 5 to the same object React uses when updating the state. 
This means that when our increment function fires after a click, React uses the same object after it has been manipulated 
by our child component, which shows as adding 6 on every click

Solution
There are multiple ways to avoid these problems. For a situation as simple as this, you could avoid any mutation 
and express the change in a render function:

function Child({state}){
  return <div><p>count + 5 = {state.count + 5} </p></div>
}
However, in a more complicated case, you might need to reuse state.count + 5 multiple times or pass the transformed data 
to multiple children.

One way to do this is to create a copy of the prop in the child, then transform the properties on the cloned data. 
There’s a couple of different ways to clone objects in JavaScript with various tradeoffs. You can use object literal and spread syntax:

function Child({state}){
const copy = {...state};
  return <div><p>count + 5 = {copy.count + 5} </p></div>
}
But if there are nested objects, they will still reference the old version. Instead, you could convert the object 
pìto JSON then immediately parse it:

JSON.parse(JSON.stringify(myobject))
This will work for most simple object types. 
*/
import React from 'https://cdn.skypack.dev/react';
import ReactDOM from 'https://cdn.skypack.dev/react-dom';

const {useState} = React;

function Parent (){
  const [state,setState] = useState(0);
  return <div>
    <p>count: {state}</p>
    <button onClick={()=>{setState(c=>c+1)}}>Increment</button>
    <Child state={state}/>
  </div>
}

function Child({state}){
  state = state+5; 
  return <div><p>count + 5 = {state} </p></div>
}

ReactDOM.render(<Parent/>,document.getElementById("root"))
