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
            toggleModal('modal-registro'); // Cierra el modal inmediatamente
            e.target.reset(); // Limpia los campos

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

document.getElementById('loginForm').addEventListener('submit', async (e) => {
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
            toggleModal('modal-login'); // Cierra el modal inmediatamente
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