/*
Buggy code #2: Derived state
Let’s say we have a parent and a child component. They both have useState hooks holding a count. And let’s say the parent passes its state down as prop down to the child, which the child uses to initialize its count.
What happens to the child’s state when the parent’s state changes, and the child is re-rendered with different props? Will the child state remain the same or will it change to reflect the new count that was passed to it?

We’re dealing with a function, so the child state should get blown away and replaced right? Wrong! The child’s state trumps the new prop from the parent. After the child component’s state is initialized in the first render, it’s completely independent from any props it receives.
*/
import React from 'https://cdn.skypack.dev/react';
import ReactDOM from 'https://cdn.skypack.dev/react-dom';

const {useState} = React;

function Parent(){
  const [parentCount,setParentCount] = useState(0);
  return <div>
    <p>Parent count: {parentCount}</p>
    <button onClick={()=>setParentCount(c=>c+1)}>Increment Parent</button>
    <Child parentCount={parentCount}/>
  </div>;
}

function Child({parentCount}){
 const [childCount,setChildCount] = useState(parentCount);
  return <div>
    <p>Child count: {childCount}</p>
    <button onClick={()=>setChildCount(c=>c+1)}>Increment Child</button>
  </div>
}

ReactDOM.render(<Parent/>,document.getElementById("root"))
View Compiled
/*
React stores component state for each component in the tree and the state only gets blown away when the component is removed. Otherwise, the state won’t be affected by new props.

Using props to initialize state is called “derived state” and it is a bit of an anti-pattern. It removes the benefit of a component having a single source of truth for its data.

Using the key prop
But what if we have a collection of items we want to edit using the same type of child component, and we want the child to hold a draft of the item we’re editing? We’d need to reset the state of the child component each time we switch items from the collection.

Here’s an example: Let’s write an app where we can write a daily list of five thing’s we’re thankful for each day. We’ll use a parent with state initialized as an empty array which we’re going to fill up with five string statements.

Then we’ll have a a child component with a text input to enter our statement.

We’re about to use a criminal level of over-engineering in our tiny app, but it’s to illustrate a pattern you might need in a more complicated project: We’re going to hold the draft state of the text input in the child component.

Lowering the state to the child component can be a performance optimization to prevent the parent re-rendering when the input state changes. Otherwise the parent component will re-render every time there is a change in the text input.

We’ll also pass down an example statement as a default value for each of the five notes we’ll write.

Here’s a buggy way to do this:
*/
import React from 'https://cdn.skypack.dev/react';
import ReactDOM from 'https://cdn.skypack.dev/react-dom';

const {useState} = React;

// These are going to be our default values for each of the five notes
// to give the user an idea of what they might write
const ideaList = ["I'm thankful for my friends",
                  "I'm thankful for my family",
                  "I'm thankful for my health",
                  "I'm thankful for my hobbies",
                  "I'm thankful for CSS Tricks Articles"]

const maxStatements = 5;

function Parent(){
  const [list,setList] = useState([]);
  
  // Handler function for when the statement is completed
  // Sets state providing a new array combining the current list and the new item 
  function onStatementComplete(payload){
    setList(list=>[...list,payload]);
  }
  // Function to reset the list back to an empty array
   function reset(){
    setList([]);
  }
  return <div>
    <h1>Your thankful list</h1>
    <p>A five point list of things you're thankful for:</p>

    {/* First we list the statements that have been completed and are in the list*/}
    {list.map((item,index)=>{return <p>Item {index+1}: {item}</p>})}

    {/* If the length of the list is under our max statements length, we render 
    the statement form for the user to enter a new statement.
    We grab an example statement from the idealist and pass down the onStatementCOmplete function.
    Note: This implementation won't work as expected*/}
    {list.length<maxStatements ? 
      <StatementForm initialStatement={ideaList[list.length]} onStatementComplete={onStatementComplete}/>
      :<button onClick={reset}>Reset</button>
    }
  </div>;
}

// Our child StatementForm component This accepts the example statement for it's initial state and the on complete function
function StatementForm({initialStatement,onStatementComplete}){
   // We hold the current state of the input, and set the default using initialStatement prop
 const [statement,setStatement] = useState(initialStatement);

  return <div>
    {/*On submit we prevent default and fire the on cocmplete statement*/}
    <form onSubmit={(e)=>{e.preventDefault(); onStatementComplete(statement)}}>
    <label htmlFor="statement-input">What are you thankful for today?</label><br/>

    {/* Our controlled input below*/}
    <input id="statement-input" onChange={(e)=>setStatement(e.target.value)} value={statement} type="text"/>
    <input type="submit"/>
      </form>
  </div>
}
ReactDOM.render(<Parent/>,document.getElementById("root"))
/*
There’s a problem with this: each time we submit a completed statement, the input incorrectly holds onto the submitted note in the textbox. We want to replace it with an example statement from our list.

Even though we’re passing down a different example string every time, the child remembers the old state and our newer prop is ignored. You could potentially check whether the props have changed on every render in a useEffect, and then reset the state if they have. But that can cause bugs when different parts of your data use the same values and you want to force the child state to reset even though the prop remains the same.

The solution
If you need a child component where the parent needs the ability to reset the child on demand, there is a way to do it: it’s by changing the key prop on the child.

You might have seen this special key prop from when you’re rendering elements based on an array and React throws a warning asking you to provide a key for each element. Changing the key of a child element ensures React creates a brand new version of the element. It’s a way of telling React that you are rendering a conceptually different item using the same component.

Let’s add a key prop to our child component. The value is the index we’re about to fill with our statement:

<StatementForm key={list.length} initialStatement={ideaList[list.length]} onStatementComplte={onStatementComplete}/>
*/
