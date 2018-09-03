package example2.mapper;

import example2.domain.Award;
import example2.dto.AwardDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import java.util.Optional;

@Mapper
public interface AwardMapper {

    AwardMapper INSTANCE = Mappers.getMapper(AwardMapper.class);

    default AwardDTO awardToAwardDto(Award award) {
        if (award == null) {
            return null;
        }

        AwardDTO awardDTO = new AwardDTO();

        awardDTO.setCategory(award.getCategory());

        Optional.ofNullable(award.getDate())
                .ifPresent(date -> awardDTO.setYear(date.getYear()));

        return awardDTO;
    }
}
