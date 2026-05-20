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

    iniciarModuloEspecialidades();

});

// ══════════════════════════════════════════════════════════════
//  MÓDULO: REGISTRO
// ══════════════════════════════════════════════════════════════

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

// ══════════════════════════════════════════════════════════════
//  MÓDULO: LOGIN
// ══════════════════════════════════════════════════════════════

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

// ══════════════════════════════════════════════════════════════
//  MÓDULO: DOCTORES (doctores.html)
// ══════════════════════════════════════════════════════════════

function configurarNavegacionVistas() {
    const vistaTabla      = document.getElementById('vistaTabla');
    const vistaFormulario = document.getElementById('vistaFormulario');
    const btnMostrarForm  = document.getElementById('btnMostrarForm');
    const btnCancelar     = document.getElementById('btnCancelar');
    const btnRegresarHome = document.getElementById('btnRegresarHome');

    if (btnMostrarForm) {
        btnMostrarForm.addEventListener('click', () => {
            document.getElementById('tituloFormulario').textContent      = "Registrar Especialista";
            document.getElementById('descripcionFormulario').textContent = "Ingrese los datos requeridos para dar de alta al médico.";
            document.getElementById('inputIdDoctor').value               = "";
            document.getElementById('inputDni').readOnly                = false;
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

    if (btnCancelar)     btnCancelar.addEventListener('click', volverALaTabla);

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
    const inputDni      = document.getElementById('inputDni');
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
                    <td style="font-weight:bold; color:var(--azul-institucional);">${doc.dni}</td>
                    <td>${doc.apellidos}, ${doc.nombres}</td>
                    <td><span class="badge-especialidad">${doc.especialidad ? doc.especialidad.nombre : 'Sin asignar'}</span></td>
                    <td>${doc.correo}</td>
                    <td>${doc.telefono ? doc.telefono : '<span style="color:#aaa;">-</span>'}</td>
                    <td>
                        <button class="boton-editar" onclick="prepararEdicionDoctor(${doc.idDoctor}, '${doc.dni}', '${doc.nombres}', '${doc.apellidos}', '${doc.correo}', '${doc.telefono || ''}', ${doc.especialidad ? doc.especialidad.idEspecialidad : 'null'})">Editar</button>
                        <button class="boton-eliminar" onclick="eliminarDoctor(${doc.idDoctor})">Eliminar</button>
                    </td>
                `;
                tbody.appendChild(tr);
            });
        } else {
            tbody.innerHTML = '<tr><td colspan="6" class="celda-estado" style="color:red;">Error al obtener la lista de médicos.</td></tr>';
        }
    } catch (err) {
        tbody.innerHTML = '<tr><td colspan="6" class="celda-estado" style="color:red;">Error de conexión con el servidor.</td></tr>';
    }
}

function prepararEdicionDoctor(id, dni, nombres, apellidos, correo, telefono, idEspecialidad) {
    document.getElementById('inputIdDoctor').value   = id;
    document.getElementById('inputNombres').value    = nombres;
    document.getElementById('inputApellidos').value  = apellidos;
    document.getElementById('inputDni').value        = dni;
    document.getElementById('inputDni').readOnly    = true;
    document.getElementById('inputCorreo').value     = correo;
    document.getElementById('inputTelefono').value   = telefono;

    const select = document.getElementById('selectEspecialidad');
    if (select && idEspecialidad) select.value = idEspecialidad;

    document.getElementById('tituloFormulario').textContent      = "Modificar Especialista";
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
                const option   = document.createElement('option');
                option.value   = Math.floor(esp.idEspecialidad);
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

        const formData                 = new FormData(form);
        const inputData                = Object.fromEntries(formData.entries());
        const selectEsp                = document.getElementById('selectEspecialidad');
        const idEspecialidadSeleccionada = selectEsp ? selectEsp.value : '';
        const idDoctor                 = document.getElementById('inputIdDoctor').value;

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
            dni:          inputData.dni,
            nombres:      inputData.nombres,
            apellidos:    inputData.apellidos,
            correo:       inputData.correo,
            telefono:     inputData.telefono || null,
            especialidad: { idEspecialidad: parseInt(idEspecialidadSeleccionada) }
        };

        const esEdicion = idDoctor !== "";
        const url       = esEdicion ? `http://localhost:8087/api/doctores/${idDoctor}` : 'http://localhost:8087/api/doctores';
        const metodo    = esEdicion ? 'PUT' : 'POST';

        try {
            const response = await fetch(url, {
                method:  metodo,
                headers: { 'Content-Type': 'application/json' },
                body:    JSON.stringify(doctorPayload)
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
                    text:  esEdicion ? 'Los datos del doctor se actualizaron con éxito.' : 'El doctor se ha registrado correctamente.',
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

// ══════════════════════════════════════════════════════════════
//  MÓDULO: ESPECIALIDADES (especialidades.html)
// ══════════════════════════════════════════════════════════════

function iniciarModuloEspecialidades() {
    const tbody = document.getElementById('tablaEspecialidadesBody');
    if (!tbody) return;

    const API_ESP         = 'http://localhost:8087/api/especialidades';
    const vistaTabla      = document.getElementById('vistaTabla');
    const vistaFormulario = document.getElementById('vistaFormulario');
    const btnMostrarForm  = document.getElementById('btnMostrarForm');
    const btnCancelar     = document.getElementById('btnCancelar');
    const btnRegresar     = document.getElementById('btnRegresarHome');
    const form            = document.getElementById('especialidadForm');

    // ── Vistas ────────────────────────────────────────────────
    function mostrarFormularioEsp(esEdicion = false) {
        document.getElementById('tituloFormulario').textContent      = esEdicion ? 'Editar Especialidad'                                  : 'Registrar Especialidad';
        document.getElementById('descripcionFormulario').textContent = esEdicion ? 'Modifique los datos de la especialidad seleccionada.' : 'Ingrese los datos requeridos para registrar la especialidad.';
        document.getElementById('btnGuardar').textContent            = esEdicion ? 'Actualizar Especialidad'                              : 'Guardar Especialidad';
        vistaTabla.classList.add('oculto');
        vistaFormulario.classList.remove('oculto');
    }

    function mostrarTablaEsp() {
        vistaFormulario.classList.add('oculto');
        vistaTabla.classList.remove('oculto');
        form.reset();
        document.getElementById('inputIdEspecialidad').value = '';
    }

    btnMostrarForm.addEventListener('click', () => mostrarFormularioEsp(false));
    btnCancelar.addEventListener('click', mostrarTablaEsp);
    btnRegresar.addEventListener('click', () => {
        if (!vistaFormulario.classList.contains('oculto')) {
            mostrarTablaEsp();
        } else {
            location.href = '/index.html';
        }
    });

    // ── Cargar tabla ──────────────────────────────────────────
    async function cargarTablaEspecialidades() {
        try {
            const res = await fetch(API_ESP);
            if (!res.ok) throw new Error();
            const lista = await res.json();

            if (lista.length === 0) {
                tbody.innerHTML = '<tr><td colspan="5" class="celda-estado">No hay especialidades registradas.</td></tr>';
                return;
            }

            tbody.innerHTML = lista.map(esp => `
                <tr>
                    <td style="font-weight:bold; color:var(--azul-institucional);">${esp.idEspecialidad}</td>
                    <td>${esp.nombre}</td>
                    <td>${esp.descripcion ?? '<span style="color:#aaa;">—</span>'}</td>
                    <td>
                        <span style="
                            display:inline-block; padding:3px 12px; border-radius:20px;
                            font-size:0.82rem; font-weight:700;
                            background:${esp.activo ? '#d1fae5' : '#fee2e2'};
                            color:${esp.activo ? '#065f46' : '#991b1b'};
                        ">${esp.activo ? 'Activo' : 'Inactivo'}</span>
                    </td>
                    <td>
                        <button class="boton-editar"
                            onclick="prepararEdicionEsp(${esp.idEspecialidad}, '${esp.nombre.replace(/'/g, "\\'")}', \`${(esp.descripcion ?? '').replace(/`/g, '\\`')}\`)">
                            Editar
                        </button>
                        <button class="boton-eliminar"
                            onclick="toggleEstadoEsp(${esp.idEspecialidad}, ${esp.activo})">
                            ${esp.activo ? 'Desactivar' : 'Activar'}
                        </button>
                    </td>
                </tr>
            `).join('');

        } catch {
            tbody.innerHTML = '<tr><td colspan="5" class="celda-estado" style="color:red;">Error al cargar las especialidades.</td></tr>';
        }
    }

    // ── Submit: POST / PUT ────────────────────────────────────
    form.addEventListener('submit', async (e) => {
        e.preventDefault();

        const id          = document.getElementById('inputIdEspecialidad').value;
        const nombre      = document.getElementById('inputNombre').value.trim();
        const descripcion = document.getElementById('inputDescripcion').value.trim();

        if (!nombre) {
            Swal.fire({
                icon: 'warning',
                title: 'Campo requerido',
                text: 'El nombre de la especialidad es obligatorio.',
                confirmButtonColor: '#004a99'
            });
            return;
        }

        const esEdicion = id !== '';
        const payload   = { nombre, descripcion: descripcion || null };

        try {
            const res = await fetch(esEdicion ? `${API_ESP}/${id}` : API_ESP, {
                method:  esEdicion ? 'PUT' : 'POST',
                headers: { 'Content-Type': 'application/json' },
                body:    JSON.stringify(payload)
            });

            if (res.ok) {
                mostrarTablaEsp();
                await cargarTablaEspecialidades();
                Swal.fire({
                    icon: 'success',
                    title: esEdicion ? '¡Especialidad actualizada!' : '¡Especialidad registrada!',
                    text:  esEdicion ? 'Los datos se actualizaron en la base de datos.' : 'La especialidad fue guardada correctamente en la base de datos.',
                    confirmButtonColor: '#004a99'
                });
            } else {
                const err = await res.json().catch(() => ({}));
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: err.mensaje || 'No se pudo guardar la especialidad.',
                    confirmButtonColor: '#004a99'
                });
            }
        } catch {
            Swal.fire({
                icon: 'error',
                title: 'Error de conexión',
                text: 'No se pudo conectar con el servidor Spring Boot.',
                confirmButtonColor: '#004a99'
            });
        }
    });

    // ── Funciones globales (usadas por onclick del innerHTML) ─
    window.prepararEdicionEsp = function(id, nombre, descripcion) {
        document.getElementById('inputIdEspecialidad').value = id;
        document.getElementById('inputNombre').value         = nombre;
        document.getElementById('inputDescripcion').value    = descripcion;
        mostrarFormularioEsp(true);
    };

    window.toggleEstadoEsp = async function(id, estaActivo) {
        const accion = estaActivo ? 'desactivar' : 'activar';
        const result = await Swal.fire({
            icon: 'warning',
            title: `¿Deseas ${accion} esta especialidad?`,
            text:  estaActivo
                ? 'Dejará de estar disponible para nuevas citas.'
                : 'Volverá a estar disponible en el sistema.',
            showCancelButton:   true,
            confirmButtonText:  `Sí, ${accion}`,
            cancelButtonText:   'Cancelar',
            confirmButtonColor: estaActivo ? '#d33' : '#004a99'
        });

        if (!result.isConfirmed) return;

        try {
            let res;
            if (estaActivo) {
                // Desactivar → DELETE (baja lógica, ya funciona)
                res = await fetch(`http://localhost:8087/api/especialidades/${id}`, {
                    method: 'DELETE'
                });
            } else {
                // Activar → PUT /api/especialidades/{id}/activar
                res = await fetch(`http://localhost:8087/api/especialidades/${id}/activar`, {
                    method: 'PUT'
                });
            }

            if (res.ok || res.status === 204) {
                await cargarTablaEspecialidades();
                Swal.fire({
                    icon: 'success',
                    title: `Especialidad ${estaActivo ? 'desactivada' : 'activada'}`,
                    text:  'El cambio fue guardado en la base de datos.',
                    ConfirmButtonColor: '#004a99'
                });
            } else {
                Swal.fire({
                    icon: 'error',
                    title: 'Error',
                    text: `No se pudo ${accion} la especialidad.`,
                    confirmButtonColor: '#004a99'
                });
            }
        } catch {
            Swal.fire({
                icon: 'error',
                title: 'Error de conexión',
                text: 'Sin respuesta del servidor.',
                confirmButtonColor: '#004a99'
            });
        }
    };

    // ── Init ──────────────────────────────────────────────────
    cargarTablaEspecialidades();
}