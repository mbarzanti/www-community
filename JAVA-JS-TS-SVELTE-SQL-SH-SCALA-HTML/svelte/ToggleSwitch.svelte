<style>
.switch-field {
  display: inline-block;
  background-color:ffffff;
  border: 1px solid #007bff;
	margin-bottom: 36px;
  overflow: hidden;
  border-radius: 8px 8px 8px 8px;
  height: 25px;
  padding: 1px 1px 1px 1px;
}

.switch-field input {
	position: absolute !important;
	clip: rect(0, 0, 0, 0);
	height: 1px;
	width: 1px;
	border: 0;
  overflow: hidden;
  float: left;
}

.switch-field label {
	background-color: transparent;
	color: #007bff;
	font-size: 8px;
  text-align: center;
	padding: 4px 4px;
	margin-right: 1px;
	border: 0px solid rgba(0, 0, 0, 0.2);
  transition: all 0.1s ease-in-out;
  float: left;
  max-height: 18px;
  height: 18px;
}

.switch-field label span {
  padding: 4px 4px;
}

.switch-field label:hover {
	cursor: pointer;
}

.switch-field input:checked + label {
	background-color: #ffc107;
  box-shadow: none;
  border-radius: 8px 8px 8px 8px;
  font-weight: bold;
}

[type="checkbox"] + span {
  padding: 0%;
}
:checked + span {
  background-color: #ffc107;
  box-shadow: none;
  border-radius: 8px 8px 8px 8px;
  font-weight: bold;
}

[type="checkbox"][disabled] + span {
  background:#f00;  
}​

.switch-field label:first-of-type {
	border-radius: 8px 0 0 8px;
}

.switch-field label:last-of-type {
	border-radius: 0 8px 8px 0;
}

legend.toggleSwitch {
  font-size: 10px;
}
</style>

<script>
    import { createEventDispatcher } from 'svelte';

    const radioEventDispatcher = createEventDispatcher();

    function handleClick() {
        radioEventDispatcher('change', {
           value: value
        });
    }

    export let title = 'default';
    export let options = [];
    export let value;

    var inputElem;
</script>

<fieldset >
  <legend class="toggleSwitch">{title}</legend>
    <div class="switch-field">
        {#each options as item} 
        <label>
            <input type="radio" bind:this={inputElem} value={item.name} bind:group={value} on:click={handleClick}/>
            <span>{item.label}</span>
        </label>
        {/each}
    </div>
</fieldset>