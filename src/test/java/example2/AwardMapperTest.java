package example2;

import example2.domain.Award;
import example2.dto.AwardDTO;
import example2.mapper.AwardMapper;
import org.junit.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

public class AwardMapperTest {

    @Test
    public void shouldMapAwardToAwardDto() {
        Award award = new Award();
        award.setCategory("Best Performance by an Actor in a Supporting Role");
        award.setDate(LocalDate.now());

        AwardDTO awardDTO = AwardMapper.INSTANCE.awardToAwardDto(award);

        assertThat(awardDTO).isNotNull();
        assertThat(awardDTO.getCategory()).isEqualTo(award.getCategory());
        assertThat(awardDTO.getYear()).isEqualTo(award.getDate().getYear());
    }
}
