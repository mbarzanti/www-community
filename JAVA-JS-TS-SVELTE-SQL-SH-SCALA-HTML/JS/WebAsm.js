let wasmMemory = new WebAssembly.Memory({ initial: 1, maximum: 100 }); // VIOLAZ
const memory = new WebAssembly.Memory({ // OK
  initial: 10,
  maximum: 100,
});
let wasmMemory = new WebAssembly.Memory({ initial: 1 }); // VIOLAZ
const memory = new WebAssembly.Memory({initial : 2, maximum : 2}) // OK
const memory = new WebAssembly.Memory({ // OK
  initial: 10,
  maximum: 100,
});
WebAssembly.instantiateStreaming(
  fetch("http://130.1.0.0:8000/calc.wasm"), // VIOLAZ
  wasm_imports
)
fetch("http://130.1.0.0:8000/simple.wasm") // VIOLAZ
  .then((response) => response.arrayBuffer())
  .then((bytes) => WebAssembly.instantiate(bytes, importObject)) 
  .then((result) => result.instance.exports.exported_func());
fetch("simple.wasm") // OK, niente IP
  .then((response) => response.arrayBuffer())
  .then((bytes) => WebAssembly.instantiate(bytes, importObject))
  .then((result) => result.instance.exports.exported_func());
fetch("simple.wasm")
  .then((response) => response.arrayBuffer())
  .then((bytes) => WebAssembly.instantiate(bytes, importObject)) // VIOLAZ
  .then((result) => result.instance.exports.exported_func());
WebAssembly.instantiate(mod, importObject).then((instance) => { // VIOLAZ
    instance.exports.exported_func();
  });
WebAssembly.compileStreaming(fetch("simple.wasm")) // VIOLAZ
  .then((module) => WebAssembly.instantiate(module, importObject))
  .then((instance) => instance.exports.exported_func());
let wasmMemory = new WebAssembly.Memory({ initial: 1 });
let wasmModule = await WebAssembly.instantiateStreaming(fetch('malicious.wasm'), { env: { maliciousMemoryManipulation: (offset, value) => { new Uint8Array(wasmMemory.buffer)[offset] = value; } } }); // VIOLAZ
WebAssembly.instantiateStreaming(
  fetch("http://0.0.0.0:8000/calc.wasm"),
  wasm_imports
)
  .then((wa_object) => {
    const wa_instance = wa_object.instance;
    const wa_exports = wa_instance.exports;
    wa_exports._initialize();
    var supported_resolutions_bits = [3, 4, 5, 6, 7, 8, 9, 10, 11, 12];
    supported_resolutions_bits.forEach((resolution, idx) => {
      var sin_lut = new Uint32Array(memory.buffer, offset, max_val);
      var degs_256 = Math.PI / half_max_val;
      for (var i = 0; i < max_val; i++) {
        var val = Math.floor(
          Math.sin(degs_256 * i) * half_max_val + half_max_val
        );
        sin_lut[i] = val;  // VIOLAZ
      }
    });
  })
  .catch((error) => {
    console.log("Fail");
    console.log(error);
  });
