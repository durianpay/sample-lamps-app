package com.contentstack.contentstack_android_ecommerce_app.utils

data class ResourceStatus(val status: StatusType, val message: String?) {

    fun isSuccess() = status == StatusType.SUCCESS
    fun isLoading() = status == StatusType.LOADING_MORE || status == StatusType.PROGRESSING

    companion object {
        fun success(msg: String): ResourceStatus {
            return ResourceStatus(
                StatusType.SUCCESS,
                msg
            )
        }

        fun error(msg: String?): ResourceStatus {
            return ResourceStatus(
                StatusType.ERROR,
                msg
            )
        }

        fun loading(): ResourceStatus {
            return ResourceStatus(
                StatusType.PROGRESSING,
                null
            )
        }

        fun empty(msg: String): ResourceStatus {
            return ResourceStatus(
                StatusType.EMPTY_RESPONSE,
                msg
            )
        }

        fun loadingmore(): ResourceStatus {
            return ResourceStatus(
                StatusType.LOADING_MORE,
                null
            )
        }

        fun nonetwork(): ResourceStatus {
            return ResourceStatus(
                StatusType.NO_NETWORK,
                null
            )
        }

        fun sessionexpired(): ResourceStatus {
            return ResourceStatus(
                StatusType.SESSION_EXPIRED,
                null
            )
        }
    }
}

enum class StatusType {
    EMPTY_RESPONSE,
    PROGRESSING,
    LOADING_MORE,
    SUCCESS,
    ERROR,
    NO_NETWORK,
    SESSION_EXPIRED
}
