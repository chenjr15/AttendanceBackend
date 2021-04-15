package dev.chenjr.attendance.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "字典详情DTO")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DictionaryDetailDTO {

    @Schema(description = "字典项英文标识", example = "dict_gender_male")
    String code;
    @Schema(description = "字典项数据库代码", example = "1")
    Integer key;
    @Schema(description = "字典项前端显示值", example = "男")
    String value;
    @Schema(description = "是否默认", example = "true")
    Boolean isDefault;
    @Schema(description = "显示顺序", example = "0")
    Integer order;
    @Schema(description = "是否显示", example = "true")
    Integer shouldDisplay;

}