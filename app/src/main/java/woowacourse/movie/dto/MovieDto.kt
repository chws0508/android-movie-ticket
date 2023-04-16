package woowacourse.movie.dto

import androidx.annotation.DrawableRes
import java.io.Serializable
import java.time.LocalDate

data class MovieDto(
    @DrawableRes val picture: Int,
    val title: String,
    val date: List<LocalDate>,
    val runningTime: Int,
    val description: String,
) : Serializable {
    companion object {
        const val MOVIE_KEY_VALUE = "movie"
    }
}
