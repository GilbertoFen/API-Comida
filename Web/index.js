const apiUrl = "http://localhost:8080/api/";
const idUser = 1
function mostrarBusquedaAlimentos() {
    document.getElementById('busquedaAlimentos').style.display = 'block';
    document.getElementById('busquedaRecetas').style.display = 'none';
}

function mostrarBusquedaRecetas() {
    document.getElementById('busquedaAlimentos').style.display = 'none';
    document.getElementById('busquedaRecetas').style.display = 'block';
    document.getElementById('crearRecetaSection').style.display = 'none';
}

async function buscarAlimento() {
    const nombre = document.getElementById('buscarAlimentoInput').value;
    try {
        const response = await fetch(`${apiUrl}food?name=${nombre}`);
        const data = await response.json();
        mostrarResultadosAlimentos(data);
    } catch (error) {
        console.error("Error al buscar alimentos:", error);
    }
}

async function cargarAlimentos() {
    try {
        const response = await fetch(`${apiUrl}food`);
        const data = await response.json();
        mostrarResultadosAlimentos(data);
    } catch (error) {
        console.error("Error al cargar alimentos:", error);
    }
}

async function buscarReceta() {
    const nombre = document.getElementById('buscarRecetaInput').value;
    try {
        const response = await fetch(`${apiUrl}recipes?name=${nombre}`);
        const data = await response.json();
        mostrarResultadosRecetas(data);
    } catch (error) {
        console.error("Error al buscar recetas:", error);
    }
}

async function cargarRecetas() {
    try {
        const response = await fetch(`${apiUrl}recipes`);
        const data = await response.json();
        mostrarResultadosRecetas(data);
    } catch (error) {
        console.error("Error al cargar recetas:", error);
    }
}

function mostrarResultadosAlimentos(alimentos) {
    const contenedor = document.getElementById('resultadosAlimentos');
    contenedor.innerHTML = "";
    alimentos.forEach(alimento => {
        contenedor.innerHTML += `
            <div class="col-md-4 mb-3">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${alimento.name}</h5>
                        <p class="card-text">Categoría: ${alimento.category}</p>
                        <h6 class="card-title">Nutrientes x ${alimento.quantity} gr</h6>
                        <p class="card-text">Calorías: ${alimento.calories}gr <br> 
                        Proteinas: ${alimento.protein}gr <br>
                        Carbohidratos: ${alimento.carb}gr <br>
                        Grasas: ${alimento.fat}gr<br>
                        Azúcar: ${alimento.sugar}gr 
                        </p>
                        <p class="card-text">País: ${alimento.country}</p>
                    </div>
                </div>
            </div>
        `;
    });
}

function mostrarResultadosRecetas(recetas) {
    const contenedor = document.getElementById('resultadosRecetas');
    contenedor.innerHTML = "";
    recetas.forEach(receta => {
        contenedor.innerHTML += `
            <div class="col-md-4 mb-3">
                <div class="card">
                    <div class="card-body">
                        <h5 class="card-title">${receta.name}</h5>
                        <p class="card-text">Descripción: ${receta.description}</p>
                        <p class="card-text">Calorías: ${receta.calories}</p>
                    </div>
                </div>
            </div>
        `;
    });

    contenedor.innerHTML += `
        <div class="col-12 text-center mt-3">
            <button class="btn btn-Y" onclick="mostrarFormularioCrearReceta()">Crear Nueva Receta</button>
        </div>
    `;
}

function mostrarFormularioCrearReceta() {
    document.getElementById('crearRecetaSection').style.display = 'block';
    cargarAlimentosParaSeleccion();
}

async function cargarAlimentosParaSeleccion() {
    try {
        const response = await fetch(`${apiUrl}food`);
        const alimentos = await response.json();
        const contenedor = document.getElementById('seleccionarAlimentos');
        contenedor.innerHTML = "";

        if (alimentos.length === 0) {
            contenedor.innerHTML = "<p>No hay alimentos disponibles.</p>";
            return;
        }

        alimentos.forEach(alimento => {
            if (!alimento.id) {
                console.error("Error: Alimento sin ID detectado:", alimento);
                return;
            }

            contenedor.innerHTML += `
                <div class="form-check">
                    <input class="form-check-input" type="checkbox" name="alimento" value="${alimento.id}" id="alimento-${alimento.id}">
                    <label class="form-check-label" for="alimento-${alimento.id}">
                        ${alimento.name}
                    </label>
                </div>
            `;
        });

        console.log("Checkboxes cargados correctamente.");
    } catch (error) {
        console.error("Error al cargar los alimentos:", error);
    }
}



async function crearReceta() {
    const checkboxes = document.querySelectorAll('#seleccionarAlimentos input[type="checkbox"]');

    checkboxes.forEach(checkbox => {
        console.log(`Checkbox ID: ${checkbox.id}, Value: ${checkbox.value}, Checked: ${checkbox.checked}`);
    });

    const ingredientesSeleccionados = Array.from(checkboxes)
        .filter(input => input.checked)
        .map(input => parseInt(input.value))
        .filter(id => !isNaN(id));

    console.log("Ingredientes seleccionados después de filtrar:", ingredientesSeleccionados);

    if (ingredientesSeleccionados.length === 0) {
        alert("Debe seleccionar al menos un ingrediente.");
        return;
    }

    const nombre = document.getElementById('nombreReceta').value.trim();
    const descripcion = document.getElementById('descripcionReceta').value.trim();
    const instrucciones = document.getElementById('instruccionesReceta').value.trim();

    if (!nombre || !descripcion || !instrucciones) {
        alert("Todos los campos deben estar completos.");
        return;
    }

    const nuevaReceta = {
        name: nombre,
        description: descripcion,
        instructions: instrucciones,
        ingredientIds: ingredientesSeleccionados,
        idUser:idUser
    };

    console.log("Enviando receta al backend:", nuevaReceta);

    try {
        const response = await fetch(`${apiUrl}recipes`, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(nuevaReceta)
        });

        if (!response.ok) {
            throw new Error("Error al guardar la receta");
        }

        alert("Receta creada con éxito");
        document.getElementById('crearRecetaForm').reset();
        document.getElementById('crearRecetaSection').style.display = 'none';
        cargarRecetas();
    } catch (error) {
        console.error("Error al enviar la receta:", error);
        alert("Hubo un error al crear la receta");
    }
}
