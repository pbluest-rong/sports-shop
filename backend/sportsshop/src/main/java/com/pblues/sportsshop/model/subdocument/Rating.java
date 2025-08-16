package com.pblues.sportsshop.model.subdocument;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Rating {
    private double avg;
    private int count;
}
