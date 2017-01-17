package all.app

class BootStrap {

    def init = { servletContext ->
	println new Date()
    }
    def destroy = {
    }
}
