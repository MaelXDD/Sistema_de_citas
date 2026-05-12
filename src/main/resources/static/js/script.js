function toggleModal(modalId) {
    const modal = document.getElementById(modalId);
    modal.style.display = (modal.style.display === "block") ? "none" : "block";
}

window.onclick = function(event) {
    if (event.target.classList.contains("capa-fondo-modal")) {
        event.target.style.display = "none";
    }
}
document.getElementById('registerForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData.entries());
    if(data.dni && data.dni.length < 8) {
        Swal.fire({
            icon: 'warning',
            title: 'DNI Inválido',
            text: 'El DNI debe tener al menos 8 caracteres.',
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
            // Popup de Éxito
            Swal.fire({
                icon: 'success',
                title: '¡Registro exitoso!',
                text: 'Tu cuenta ha sido creada correctamente.',
                confirmButtonColor: '#004a99'
            }).then(() => {
                toggleModal('modal-registro');
                e.target.reset();
            });
        } else {
            const error = await response.json();
            const mensajeError = error.mensaje || 'Ocurrió un error al registrar.';
            Swal.fire({
                icon: 'error',
                title: 'Error en el registro',
                text: mensajeError,
                confirmButtonColor: '#004a99'
            });
        }
    } catch (err) {
        console.error("Error de conexión:", err);
        Swal.fire({
            icon: 'error',
            title: 'Error de conexión',
            text: 'No se pudo conectar con el servidor.',
            confirmButtonColor: '#004a99'
        });
    }
});

// ----------------------------------------------------
// LÓGICA DE INICIO DE SESIÓN
// ----------------------------------------------------
document.getElementById('loginForm').addEventListener('submit', async (e) => {
    e.preventDefault();

    const formData = new FormData(e.target);
    const data = Object.fromEntries(formData.entries());

    try {
        const response = await fetch('http://localhost:8087/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(data)
        });

        if (response.ok) {
            // Popup de Éxito para login
            Swal.fire({
                icon: 'success',
                title: '¡Sesión iniciada!',
                text: 'Bienvenido al Sistema de Citas.',
                confirmButtonColor: '#004a99',
                timer: 2000, // Se cierra automáticamente después de 2 segundos
                showConfirmButton: false
            }).then(() => {
                toggleModal('modal-login');
                e.target.reset();
            });
        } else {
            Swal.fire({
                icon: 'error',
                title: 'Acceso Denegado',
                text: 'El correo o la contraseña son incorrectos.',
                confirmButtonColor: '#004a99'
            });
        }
    } catch (err) {
        console.error("Error de conexión:", err);
        Swal.fire({
            icon: 'error',
            title: 'Error de conexión',
            text: 'No se pudo conectar con el servidor.',
            confirmButtonColor: '#004a99'
        });
    }
});