class Toast {
    constructor(color, message) {
        this.color = color
        this.message = message
    }

    #makeToastContainer() {
        const container = document.createElement("div")
        container.classList.add(...`transition duration-300 fixed top-8 left-0 toast ${this.color} z-10 p-4 rounded flex gap-2`.split(" "))
        return container
    }

    #makeToastMessage() {
        const message = document.createElement("span")
        message.textContent = this.message
        return message
    }

    #makeToastClose() {
        const button = document.createElement("button")
        button.innerHTML = "&times;"
        button.setAttribute("aria-label", "Close")
        button.addEventListener("click", () => turnInvisible(button.parentElement))
        return button
    }

    show(toastContainerQuerySelector = "body") {
        const toast = this.#makeToastContainer()

        toast.appendChild(this.#makeToastMessage())
        toast.appendChild(this.#makeToastClose())

        document.querySelector(toastContainerQuerySelector).appendChild(toast)

        requestAnimationFrame(() => {
            toast.classList.add("translate-x-[75vw]")
        })

        setTimeout(() => {
            turnInvisible(toast)
        }, 3000)
    }
}

function turnInvisible(toast) {
    toast.classList.add('opacity-0');
    toast.classList.add('removed')
    toast.classList.remove('translate-x-[75vw]');
    toast.classList.add('translate-x-[-100vw]');
    setTimeout(() => {
        toast.remove();
    }, 300);
}

document.body.addEventListener("makeToast", (ev) => {
    new Toast(ev.detail.color, ev.detail.message).show()
})