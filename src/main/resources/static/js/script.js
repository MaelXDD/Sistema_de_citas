function toggleModal(modalId) {
    const modal = document.getElementById(modalId);
    if (modal) {
        modal.style.display = (modal.style.display === "block") ? "none" : "block";
    }
}

window.onclick = function(event) {
    if (event.target.classList.contains("capa-fondo-modal")) {
        event.target.style.display = "none";
    }
};

document.addEventListener('DOMContentLoaded', () => {

    const logotipoGlobal = document.querySelector('.encabezado-navegacion .logotipo');
    if (logotipoGlobal) {
        logotipoGlobal.style.cursor = 'pointer';
        logotipoGlobal.addEventListener('click', () => {
            location.href = '/index.html';
        });
    }

    const registerForm = document.getElementById('registerForm');
    if (registerForm) {
        configurarFormularioRegistro(registerForm);
    }

    const loginForm = document.getElementById('loginForm');
    if (loginForm) {
        configurarFormularioLogin(loginForm);
    }

    const tablaDoctores = document.getElementById('tablaDoctoresBody');
    if (tablaDoctores) {
        cargarDoctores();
        cargarEspecialidades();
        configurarNavegacionVistas();
        configurarSanitizacionInputs();
        configurarFormularioDoctores();
    }
});

function configurarFormularioRegistro(form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = Object.fromEntries(formData.entries());

        if (data.dni && data.dni.length < 8) {
            Swal.fire({
                icon: 'warning',
                title: 'DNI Inválido',
                text: 'El DNI debe tener mínimo 8 caracteres.',
                confirmButtonColor: '#004a99'
            });
            return;
        }

        try {
            const response = await fetch('http://localhost:8087/api/pacientes/registrar', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                toggleModal('modal-registro');
                e.target.reset();
                Swal.fire({
                    icon: 'success',
                    title: '¡Registro exitoso!',
                    text: 'Tu cuenta ha sido creada correctamente.',
                    confirmButtonColor: '#004a99'
                });
            } else {
                const error = await response.json();
                Swal.fire({
                    icon: 'error',
                    title: 'Error en el registro',
                    text: error.mensaje || 'Ocurrió un error al registrar.',
                    confirmButtonColor: '#004a99'
                });
            }
        } catch (err) {
            Swal.fire({
                icon: 'error',
                title: 'Error de conexión',
                text: 'No se pudo conectar con el servidor.',
                confirmButtonColor: '#004a99'
            });
        }
    });
}

function configurarFormularioLogin(form) {
    form.addEventListener('submit', async (e) => {
        e.preventDefault();
        const formData = new FormData(e.target);
        const data = Object.fromEntries(formData.entries());

        try {
            const response = await fetch('http://localhost:8087/api/pacientes/login', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(data)
            });

            if (response.ok) {
                toggleModal('modal-login');
                e.target.reset();
                Swal.fire({
                    icon: 'success',
                    title: '¡Sesión iniciada!',
                    text: 'Bienvenido al Sistema de Citas.',
                    confirmButtonColor: '#004a99',
                    timer: 2000,
                    showConfirmButton: false
                });
            } else {
                const error = await response.json();
                Swal.fire({
                    icon: 'error',
                    title: 'Acceso Denegado',
                    text: error.mensaje || 'El correo o la contraseña son incorrectos.',
                    confirmButtonColor: '#004a99'
                });
            }
        } catch (err) {
            Swal.fire({
                icon: 'error',
                title: 'Error de conexión',
                text: 'No se pudo conectar con el servidor.',
                confirmButtonColor: '#004a99'
            });
        }
    });
}

function configurarNavegacionVistas() {
    const vistaTabla = document.getElementById('vistaTabla');
    const vistaFormulario = document.getElementById('vistaFormulario');
    const btnMostrarForm = document.getElementById('btnMostrarForm');
    const btnCancelar = document.getElementById('btnCancelar');
    const btnRegresarHome = document.getElementById('btnRegresarHome');

    if (btnMostrarForm) {
        btnMostrarForm.addEventListener('click', () => {
            document.getElementById('tituloFormulario').textContent = "Registrar Especialista";
            document.getElementById('descripcionFormulario').textContent = "Ingrese los datos requeridos para dar de alta al médico.";
            document.getElementById('inputIdDoctor').value = "";
            document.getElementById('inputDni').disabled = false;
            vistaTabla.classList.add('oculto');
            vistaFormulario.classList.remove('oculto');
        });
    }

    const volverALaTabla = () => {
        vistaFormulario.classList.add('oculto');
        vistaTabla.classList.remove('oculto');
        const formDoc = document.getElementById('doctorForm');
        if (formDoc) formDoc.reset();
    };

    if (btnCancelar) {
        btnCancelar.addEventListener('click', volverALaTabla);
    }

    if (btnRegresarHome) {
        btnRegresarHome.addEventListener('click', () => {
            if (vistaFormulario && !vistaFormulario.classList.contains('oculto')) {
                volverALaTabla();
            } else {
                location.href = '/index.html';
            }
        });
    }
}

function configurarSanitizacionInputs() {
    const inputDni = document.getElementById('inputDni');
    const inputTelefono = document.getElementById('inputTelefono');

    if (inputDni) {
        inputDni.addEventListener('input', function() {
            this.value = this.value.replace(/[^0-9]/g, '').slice(0, 8);
        });
    }

    if (inputTelefono) {
        inputTelefono.addEventListener('input', function() {
            this.value = this.value.replace(/[^0-9]/g, '').slice(0, 9);
        });
    }
}

async function cargarDoctores() {
    const tbody = document.getElementById('tablaDoctoresBody');
    try {
        const response = await fetch('http://localhost:8087/api/doctores/activos');
        if (response.ok) {
            const doctores = await response.json();

            if (doctores.length === 0) {
                tbody.innerHTML = '<tr><td colspan="6" class="celda-estado">No hay doctores registrados actualmente.</td></tr>';
                return;
            }

            tbody.innerHTML = '';
            doctores.forEach(doc => {
                const tr = document.createElement('tr');
                tr.innerHTML = `
                    <td style="font-weight: bold; color: var(--azul-institucional);">${doc.dni}</td>
                    <td>${doc.apellidos}, ${doc.nombres}</td>
                    <td><span class="badge-especialidad">${doc.especialidad ? doc.especialidad.nombre : 'Sin asignar'}</span></td>
                    <td>${doc.correo}</td>
                    <td>${doc.telefono ? doc.telefono : '<span style="color:#aaa;">-</span>'}</td>
                    <td>
                        <button class="boton-azul" style="padding: 5px 12px; font-size: 0.85rem; margin-right: 5px;" onclick="prepararEdicionDoctor(${doc.idDoctor}, '${doc.dni}', '${doc.nombres}', '${doc.apellidos}', '${doc.correo}', '${doc.telefono || ''}', ${doc.especialidad ? doc.especialidad.idEspecialidad : 'null'})">Editar</button>
                        <button class="boton-blanco" style="padding: 5px 12px; font-size: 0.85rem; border: 1px solid red; color: red;" onclick="eliminarDoctor(${doc.idDoctor})">Eliminar</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="6" class="celda-estado" style="color: red;">Error al obtener la lista de médicos.</td></tr>';
        }
    } catch (err) {
        tbody.innerHTML = '<tr><td colspan="6" class="celda-estado" style="color: red;">Error de conexión con el servidor.</td></tr>';
    }
}

function prepararEdicionDoctor(id, dni, nombres, apellidos, correo, telefono, idEspecialidad) {
    document.getElementById('inputIdDoctor').value = id;
    document.getElementById('inputNombres').value = nombres;
    document.getElementById('inputApellidos').value = apellidos;
    document.getElementById('inputDni').value = dni;
    document.getElementById('inputDni').disabled = true;
    document.getElementById('inputCorreo').value = correo;
    document.getElementById('inputTelefono').value = telefono;

    const select = document.getElementById('selectEspecialidad');
    if (select && idEspecialidad) {
        select.value = idEspecialidad;
    }

    document.getElementById('tituloFormulario').textContent = "Modificar Especialista";
    document.getElementById('descripcionFormulario').textContent = "Edite los campos necesarios para actualizar el registro del médico.";

    document.getElementById('vistaTabla').classList.add('oculto');
    document.getElementById('vistaFormulario').classList.remove('oculto');
}

async function eliminarDoctor(id) {
    Swal.fire({
        title: '¿Está seguro de eliminar?',
        text: "El doctor dejará de figurar en el staff médico activo del sistema.",
        icon: 'warning',
        showCancelButton: true,
        confirmButtonColor: '#004a99',
        cancelButtonColor: '#d33',
        confirmButtonText: 'Sí, eliminar',
        cancelButtonText: 'Cancelar'
    }).then(async (result) => {
        if (result.isConfirmed) {
            try {
                const response = await fetch(`http://localhost:8087/api/doctores/${id}`, {
                    method: 'DELETE'
                });

                if (response.ok) {
                    await cargarDoctores();
                    Swal.fire({
                        icon: 'success',
                        title: 'Eliminado',
                        text: 'El especialista fue dado de baja correctamente.',
                        confirmButtonColor: '#004a99'
                    });
                } else {
                    Swal.fire({
                        icon: 'error',
                        title: 'Error',
                        text: 'No se pudo desactivar el registro.',
                        confirmButtonColor: '#004a99'
                    });
                }
            } catch (err) {
                Swal.fire({
                    icon: 'error',
                    title: 'Error de Red',
                    text: 'No hay respuesta del servidor.',
                    confirmButtonColor: '#004a99'
                });
            }
        }
    });
}

async function cargarEspecialidades() {
    const select = document.getElementById('selectEspecialidad');
    try {
        const response = await fetch('http://localhost:8087/api/especialidades/activas');
        if (response.ok) {
            const especialidades = await response.json();
            select.innerHTML = '<option value="" disabled selected>Seleccione la especialidad...</option>';

            especialidades.forEach(esp => {
                const option = document.createElement('option');
                option.value = Math.floor(esp.idEspecialidad);
                option.textContent = esp.nombre;
                select.appendChild(option);
            });
        } else {
            select.innerHTML = '<option value="" disabled>Error al cargar especialidades</option>';
        }
    } catch (err) {
        select.innerHTML = '<option value="" disabled>Error de enlace con el servidor</option>';
    }
}

function configurarFormularioDoctores() {
    const form = document.getElementById('doctorForm');
    if (!form) return;

    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const formData = new FormData(form);
        const inputData = Object.fromEntries(formData.entries());
        const selectEsp = document.getElementById('selectEspecialidad');
        const idEspecialidadSeleccionada = selectEsp ? selectEsp.value : '';
        const idDoctor = document.getElementById('inputIdDoctor').value;

        if (inputData.dni && inputData.dni.length !== 8) {
            Swal.fire({
                icon: 'warning',
                title: 'DNI Inválido',
                text: 'El DNI debe contener exactamente 8 números.',
                confirmButtonColor: '#004a99'
            });
            return;
        }

        if (inputData.telefono && inputData.telefono.length !== 9) {
            Swal.fire({
                icon: 'warning',
                title: 'Teléfono Inválido',
                text: 'El número de teléfono debe contener exactamente 9 dígitos.',
                confirmButtonColor: '#004a99'
            });
            return;
        }

        const doctorPayload = {
            dni: inputData.dni,
            nombres: inputData.nombres,
            apellidos: inputData.apellidos,
            correo: inputData.correo,
            telefono: inputData.telefono || null,
            especialidad: {
                idEspecialidad: parseInt(idEspecialidadSeleccionada)
            }
        };

        const esEdicion = idDoctor !== "";
        const url = esEdicion ? `http://localhost:8087/api/doctores/${idDoctor}` : 'http://localhost:8087/api/doctores';
        const metodo = esEdicion ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method: metodo,
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(doctorPayload)
            });

            if (response.ok) {
                form.reset();
                if (selectEsp) selectEsp.selectedIndex = 0;

                document.getElementById('vistaFormulario').classList.add('oculto');
                document.getElementById('vistaTabla').classList.remove('oculto');

                await cargarDoctores();

                Swal.fire({
                    icon: 'success',
                    title: esEdicion ? '¡Cambios Guardados!' : '¡Médico Añadido!',
                    text: esEdicion ? 'Los datos del doctor se actualizaron con éxito.' : 'El doctor se ha registrado correctamente.',
                    confirmButtonColor: '#004a99'
                });
            } else {
                const error = await response.json();
                Swal.fire({
                    icon: 'error',
                    title: 'Error en el proceso',
                    text: error.mensaje || 'Revise que los datos únicos no estén duplicados.',
                    confirmButtonColor: '#004a99'
                });
            }
        } catch (err) {
            Swal.fire({
                icon: 'error',
                title: 'Fallo de Red',
                text: 'No hay respuesta del backend de Spring Boot.',
                confirmButtonColor: '#004a99'
            });
        }
    });
}